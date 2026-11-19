import { EmissionsTradingSchemeDataInputEnergyFuelDetails } from 'cca-api';

import { ETS_ENERGY_FUEL_DETAILS_SUBTASK } from './ets-reporting.types';
import { canSubmitEtsReporting } from './ets-reporting.validation';
import { createEtsFuelDetails, mapEtsStandardFuelRows } from './ets-reporting-calculations';
import { mockCompletedEtsPayload, mockEtsFuelPayload } from './testing/mock-data';

function completedFuelPayload() {
  const payload = mockEtsFuelPayload();
  payload.sectionsCompleted[ETS_ENERGY_FUEL_DETAILS_SUBTASK] = 'COMPLETED';
  payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails = createEtsFuelDetails('ENERGY_KWH', [
    { ...mapEtsStandardFuelRows('ENERGY_KWH')[0], deliveredEnergy: '1000' },
    {
      fuelKey: null,
      fuelType: 'Additional fuel',
      co2ConversionFactor: '0.5',
      deliveredEnergy: '100',
      primaryEnergyConversionFactor: '1',
      isCustom: true,
    },
  ]);
  return payload;
}

describe('ETS submission validation against the Java DTO contract', () => {
  it.each<[string, (details: EmissionsTradingSchemeDataInputEnergyFuelDetails) => void]>([
    ['null standard fuel collection', (details) => (details.standardFuels = null)],
    ['null custom fuel collection', (details) => (details.nonStandardFuels = null)],
    ['null standard fuel row', (details) => (details.standardFuels.NATURAL_GAS = null)],
    ['null custom fuel row', (details) => (details.nonStandardFuels[0] = null)],
    ['missing standard delivered energy', (details) => delete details.standardFuels.NATURAL_GAS.deliveredEnergy],
    ['missing standard primary energy', (details) => delete details.standardFuels.NATURAL_GAS.primaryEnergy],
    ['missing custom name', (details) => delete details.nonStandardFuels[0].name],
    ['missing custom conversion factor', (details) => delete details.nonStandardFuels[0].conversionFactor],
    ['missing custom delivered energy', (details) => delete details.nonStandardFuels[0].deliveredEnergy],
    ['missing custom primary energy', (details) => delete details.nonStandardFuels[0].primaryEnergy],
    ['missing primary total', (details) => delete details.totalPrimaryEnergy],
    ['missing emissions total', (details) => delete details.totalCo2Emissions],
    ['negative primary total', (details) => (details.totalPrimaryEnergy = '-1')],
    ['negative emissions total', (details) => (details.totalCo2Emissions = '-1')],
    ['blank custom name', (details) => (details.nonStandardFuels[0].name = ' ')],
    [
      'custom name over 255 characters including whitespace',
      (details) => (details.nonStandardFuels[0].name = ` ${'a'.repeat(255)}`),
    ],
    ['negative custom factor', (details) => (details.nonStandardFuels[0].conversionFactor = '-0.1')],
    ['zero standard consumption', (details) => (details.standardFuels.NATURAL_GAS.deliveredEnergy = '0')],
    ['zero custom consumption', (details) => (details.nonStandardFuels[0].deliveredEnergy = '0')],
    [
      'standard consumption over seven decimals',
      (details) => (details.standardFuels.NATURAL_GAS.deliveredEnergy = '0.12345678'),
    ],
    ['custom factor over seven decimals', (details) => (details.nonStandardFuels[0].conversionFactor = '0.12345678')],
    [
      'custom consumption over seven decimals',
      (details) => (details.nonStandardFuels[0].deliveredEnergy = '0.12345678'),
    ],
    ['primary total over seven decimals', (details) => (details.totalPrimaryEnergy = '0.12345678')],
    ['emissions total over seven decimals', (details) => (details.totalCo2Emissions = '0.12345678')],
    ['incorrect standard primary energy', (details) => (details.standardFuels.NATURAL_GAS.primaryEnergy = '999')],
    ['incorrect custom primary energy', (details) => (details.nonStandardFuels[0].primaryEnergy = '99')],
  ])('rejects %s despite a completed section', (_, invalidate) => {
    const payload = completedFuelPayload();
    expect(canSubmitEtsReporting(payload)).toBe(true);
    invalidate(payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails);
    expect(canSubmitEtsReporting(payload)).toBe(false);
  });

  it.each([undefined, null, true, false])('requires the correct voluntary-answer branch for %s', (answer) => {
    const payload = completedFuelPayload();
    const input = payload.emissionsTradingSchemeDataInputDetails;
    input.provideFuelDetails = answer;
    expect(canSubmitEtsReporting(payload)).toBe(answer == null);

    input.isUltraSmallEmitter = true;
    expect(canSubmitEtsReporting(payload)).toBe(answer === true);
  });

  it('requires an answered USE question and accepts opt-out only without a fuel object', () => {
    const payload = mockCompletedEtsPayload();
    expect(canSubmitEtsReporting(payload)).toBe(true);
    payload.emissionsTradingSchemeDataInputDetails.isUltraSmallEmitter = null;
    expect(canSubmitEtsReporting(payload)).toBe(false);
    payload.emissionsTradingSchemeDataInputDetails.isUltraSmallEmitter = true;
    payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails =
      completedFuelPayload().emissionsTradingSchemeDataInputDetails.energyFuelDetails;
    expect(canSubmitEtsReporting(payload)).toBe(false);
  });

  it.each(['ENERGY_KWH', 'ENERGY_MWH', 'ENERGY_GJ', 'CARBON_KG', 'CARBON_TONNE'] as const)(
    'preserves valid seven-decimal inputs and flat seven-decimal totals for %s',
    (measurementType) => {
      const payload = completedFuelPayload();
      payload.measurementType = measurementType;
      const details = createEtsFuelDetails(measurementType, [
        { ...mapEtsStandardFuelRows(measurementType)[0], deliveredEnergy: '0.0000001' },
      ]);
      payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails = details;
      expect(canSubmitEtsReporting(payload)).toBe(true);
      expect(Object.keys(details).sort()).toEqual([
        'nonStandardFuels',
        'standardFuels',
        'totalCo2Emissions',
        'totalPrimaryEnergy',
      ]);
      expect(details.totalPrimaryEnergy).toMatch(/^\d+(\.\d{1,7})?$/);
      expect(details.totalCo2Emissions).toMatch(/^\d+(\.\d{1,7})?$/);
    },
  );
});
