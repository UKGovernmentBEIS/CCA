import { freeze } from 'immer';

import { EmissionsTradingSchemeDataInputDetails } from 'cca-api';

import {
  createEtsSaveDto,
  createEtsSubmitDto,
  removeEtsNonStandardFuel,
  updateEtsFuelDetails,
  updateEtsUseAnswers,
} from './ets-reporting.transform';
import { ETS_ENERGY_FUEL_DETAILS_SUBTASK, EtsFuelRowInput, EtsReportingTaskPayload } from './ets-reporting.types';
import {
  areEtsUseAnswersComplete,
  canSubmitEtsReporting,
  isEtsReportingComplete,
  isValidEtsDecimal,
  requiresEtsFuelDetails,
} from './ets-reporting.validation';
import { createEtsFuelDetails, mapEtsStandardFuelRows } from './ets-reporting-calculations';
import { mockEtsPayload } from './testing/mock-data';

function validPayload(): EtsReportingTaskPayload {
  const rows = mapEtsStandardFuelRows('ENERGY_KWH');
  rows[0].deliveredEnergy = '1000';
  return {
    payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_PAYLOAD',
    targetYear: 2026,
    uketsId: 'UKETS-123',
    measurementType: 'ENERGY_KWH',
    emissionsTradingSchemeDataInputDetails: {
      isUltraSmallEmitter: false,
      energyFuelDetails: createEtsFuelDetails('ENERGY_KWH', rows),
    },
    sectionsCompleted: { unrelatedSection: 'COMPLETED', [ETS_ENERGY_FUEL_DETAILS_SUBTASK]: 'COMPLETED' },
  };
}

describe('UK ETS save transformations', () => {
  it('deletes only the selected custom fuel, recalculates totals and requires reconfirmation without mutating frozen state', () => {
    const payload = validPayload();
    payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails.nonStandardFuels = [
      { name: 'First', conversionFactor: '0.5', deliveredEnergy: '100', primaryEnergy: '100' },
      { name: 'Second', conversionFactor: '0.25', deliveredEnergy: '200', primaryEnergy: '200' },
    ];
    const original = freeze(payload, true);
    const updated = removeEtsNonStandardFuel(original, 0);
    expect(updated.emissionsTradingSchemeDataInputDetails.energyFuelDetails).toEqual({
      standardFuels: original.emissionsTradingSchemeDataInputDetails.energyFuelDetails.standardFuels,
      nonStandardFuels: [{ name: 'Second', conversionFactor: '0.25', deliveredEnergy: '200', primaryEnergy: '200' }],
      totalPrimaryEnergy: '1200',
      totalCo2Emissions: '0.23254',
    });
    expect(updated.sectionsCompleted).toEqual({ unrelatedSection: 'COMPLETED', etsEnergyFuelDetails: 'IN_PROGRESS' });
    expect(updated.measurementType).toBe(original.measurementType);
    expect(original.emissionsTradingSchemeDataInputDetails.energyFuelDetails.nonStandardFuels).toHaveLength(2);
    expect(canSubmitEtsReporting(updated)).toBe(false);
    expect(isEtsReportingComplete(updated)).toBe(true);
  });

  it.each(['CARBON_KG', 'CARBON_TONNE'] as const)(
    'does not allow completion after deleting the only consumption for %s',
    (measurementType) => {
      const payload = validPayload();
      payload.measurementType = measurementType;
      payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails = {
        nonStandardFuels: [{ name: 'Only fuel', conversionFactor: '0.5', deliveredEnergy: '100', primaryEnergy: '50' }],
        totalPrimaryEnergy: '50',
        totalCo2Emissions: '0.05',
      };
      const updated = removeEtsNonStandardFuel(freeze(payload, true), 0);
      expect(updated.emissionsTradingSchemeDataInputDetails.energyFuelDetails).toEqual({
        nonStandardFuels: [],
        totalPrimaryEnergy: '0',
        totalCo2Emissions: '0',
      });
      expect(isEtsReportingComplete(updated)).toBe(false);
      expect(canSubmitEtsReporting(updated)).toBe(false);
    },
  );

  it.each([-1, 0, 0.5, NaN])('ignores a deletion that does not identify a saved custom fuel: %s', (index) => {
    const payload = freeze(validPayload(), true);
    expect(removeEtsNonStandardFuel(payload, index)).toBe(payload);
  });

  it('preserves fuels across both fuel-required branches and removes the irrelevant voluntary answer', () => {
    const original = freeze(validPayload(), true);
    const voluntary = updateEtsUseAnswers(original, true, true);
    const nonUse = updateEtsUseAnswers(voluntary, false);
    expect(voluntary.emissionsTradingSchemeDataInputDetails).toEqual({
      isUltraSmallEmitter: true,
      provideFuelDetails: true,
      energyFuelDetails: original.emissionsTradingSchemeDataInputDetails.energyFuelDetails,
    });
    expect(nonUse.emissionsTradingSchemeDataInputDetails).toEqual(original.emissionsTradingSchemeDataInputDetails);
    expect(nonUse.sectionsCompleted).toEqual({ unrelatedSection: 'COMPLETED', etsEnergyFuelDetails: 'IN_PROGRESS' });
    expect(original.sectionsCompleted.etsEnergyFuelDetails).toBe('COMPLETED');
  });

  it('clears fuels and totals on opt-out and starts re-entry with empty default rows', () => {
    const original = freeze(validPayload(), true);
    const optedOut = updateEtsUseAnswers(original, true, false);
    const optedIn = updateEtsUseAnswers(optedOut, true, true);
    expect(optedOut.emissionsTradingSchemeDataInputDetails).toEqual({
      isUltraSmallEmitter: true,
      provideFuelDetails: false,
    });
    expect(optedIn.emissionsTradingSchemeDataInputDetails.energyFuelDetails).toBeUndefined();
    expect(
      mapEtsStandardFuelRows(
        optedIn.measurementType,
        optedIn.emissionsTradingSchemeDataInputDetails.energyFuelDetails?.standardFuels,
      ).every((row) => row.deliveredEnergy === '0'),
    ).toBe(true);
    expect(original.emissionsTradingSchemeDataInputDetails.energyFuelDetails.totalPrimaryEnergy).toBe('1000');
  });

  it('preserves existing answers and unrelated sections when saving fuel changes', () => {
    const original = freeze(updateEtsUseAnswers(validPayload(), true, true), true);
    const rows = mapEtsStandardFuelRows('ENERGY_KWH');
    rows[0].deliveredEnergy = '2000';
    const fuelDetails = createEtsFuelDetails('ENERGY_KWH', rows);
    const updated = updateEtsFuelDetails(original, fuelDetails);
    const dto = createEtsSaveDto(56, updated);
    expect(dto).toEqual({
      requestTaskId: 56,
      requestTaskActionType: 'EMISSIONS_TRADING_SCHEME_DATA_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_SAVE_PAYLOAD',
        emissionsTradingSchemeDataInputDetails: {
          isUltraSmallEmitter: true,
          provideFuelDetails: true,
          energyFuelDetails: fuelDetails,
        },
        sectionsCompleted: { unrelatedSection: 'COMPLETED', etsEnergyFuelDetails: 'IN_PROGRESS' },
      },
    });
    expect(updated.targetYear).toBe(2026);
    expect(updated.uketsId).toBe('UKETS-123');
    expect(original.emissionsTradingSchemeDataInputDetails.energyFuelDetails.totalPrimaryEnergy).toBe('1000');
  });

  it('saves initial answers without manufacturing fuel data and preserves corrections loaded by the API', () => {
    const initial = updateEtsUseAnswers(mockEtsPayload(), false);
    expect(createEtsSaveDto(56, initial).requestTaskActionPayload.emissionsTradingSchemeDataInputDetails).toEqual({
      isUltraSmallEmitter: false,
    });
    const correction = validPayload();
    expect(
      mapEtsStandardFuelRows(
        correction.measurementType,
        correction.emissionsTradingSchemeDataInputDetails.energyFuelDetails.standardFuels,
      )[0].deliveredEnergy,
    ).toBe('1000');
    expect(createEtsSaveDto(56, correction, 'COMPLETED').requestTaskActionPayload.sectionsCompleted).toEqual({
      unrelatedSection: 'COMPLETED',
      etsEnergyFuelDetails: 'COMPLETED',
    });
  });

  it('creates the declared empty-payload submit action', () => {
    expect(createEtsSubmitDto(56)).toEqual({
      requestTaskId: 56,
      requestTaskActionType: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_APPLICATION',
      requestTaskActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
    });
  });
});

describe('UK ETS branch validity', () => {
  it.each([' natural GAS ', 'lpg', 'Gas oil / Diesel'])(
    'blocks completion and submit for a persisted standard/custom name collision: %s',
    (name) => {
      const payload = validPayload();
      payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails = createEtsFuelDetails('ENERGY_KWH', [
        {
          fuelKey: null,
          fuelType: name,
          co2ConversionFactor: '0',
          deliveredEnergy: '100',
          primaryEnergyConversionFactor: '1',
          isCustom: true,
        },
      ]);
      expect(isEtsReportingComplete(payload)).toBe(false);
      expect(canSubmitEtsReporting(payload)).toBe(false);
    },
  );

  it.each<[EmissionsTradingSchemeDataInputDetails, boolean, boolean]>([
    [undefined, false, false],
    [{ isUltraSmallEmitter: false }, true, true],
    [{ isUltraSmallEmitter: true }, false, false],
    [{ isUltraSmallEmitter: true, provideFuelDetails: true }, true, true],
    [{ isUltraSmallEmitter: true, provideFuelDetails: false }, true, false],
  ])('resolves the USE branch for %j', (input, answered, fuelsRequired) => {
    expect(areEtsUseAnswersComplete(input)).toBe(answered);
    expect(requiresEtsFuelDetails(input)).toBe(fuelsRequired);
  });

  it('allows only fully valid completed branches to submit', () => {
    const payload = validPayload();
    expect(isEtsReportingComplete(payload)).toBe(true);
    expect(canSubmitEtsReporting(payload)).toBe(true);
    payload.sectionsCompleted.etsEnergyFuelDetails = 'IN_PROGRESS';
    expect(isEtsReportingComplete(payload)).toBe(true);
    expect(canSubmitEtsReporting(payload)).toBe(false);
    expect(
      canSubmitEtsReporting(
        mockEtsPayload({
          sectionsCompleted: { etsEnergyFuelDetails: 'COMPLETED' },
        }),
      ),
    ).toBe(false);
  });

  it('accepts the opt-out branch without totals but rejects stale fuels', () => {
    const payload = updateEtsUseAnswers(validPayload(), true, false);
    expect(isEtsReportingComplete(payload)).toBe(true);
    expect(
      isEtsReportingComplete(
        mockEtsPayload({
          emissionsTradingSchemeDataInputDetails: { isUltraSmallEmitter: true, provideFuelDetails: false },
        }),
      ),
    ).toBe(true);
    const stale = {
      ...payload,
      emissionsTradingSchemeDataInputDetails: {
        ...payload.emissionsTradingSchemeDataInputDetails,
        energyFuelDetails: validPayload().emissionsTradingSchemeDataInputDetails.energyFuelDetails,
      },
    };
    expect(isEtsReportingComplete(stale)).toBe(false);
  });

  it('rejects missing fuel data, all-zero fuel data and stale calculations', () => {
    const payload = validPayload();
    expect(isEtsReportingComplete(updateEtsUseAnswers(mockEtsPayload(), false))).toBe(false);
    expect(
      isEtsReportingComplete(
        updateEtsFuelDetails(payload, createEtsFuelDetails('ENERGY_KWH', mapEtsStandardFuelRows('ENERGY_KWH'))),
      ),
    ).toBe(false);
    const stale = validPayload();
    stale.emissionsTradingSchemeDataInputDetails.energyFuelDetails.totalCo2Emissions = '3';
    expect(isEtsReportingComplete(stale)).toBe(false);
  });

  it.each(['GRID_ELECTRICITY', 'NON_GRID_ELECTRICITY', 'NITROGEN_COOLING', 'CARBON_DIOXIDE_COOLING', 'UNKNOWN'])(
    'rejects an unsupported standard fuel %s',
    (key) => {
      const payload = validPayload();
      payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails.standardFuels[key] = {
        deliveredEnergy: '1',
        primaryEnergy: '1',
      };
      expect(isEtsReportingComplete(payload)).toBe(false);
    },
  );

  it('accepts positive zero-factor custom fuels and rejects duplicate or too many custom rows', () => {
    const fuel: EtsFuelRowInput = {
      fuelKey: null,
      fuelType: 'Biomass',
      co2ConversionFactor: '0',
      deliveredEnergy: '10',
      primaryEnergyConversionFactor: '1',
      isCustom: true,
    };
    const payload = mockEtsPayload({
      measurementType: 'CARBON_KG',
      emissionsTradingSchemeDataInputDetails: {
        isUltraSmallEmitter: false,
        energyFuelDetails: createEtsFuelDetails('CARBON_KG', [fuel]),
      },
    });
    expect(isEtsReportingComplete(payload)).toBe(true);
    const duplicates = createEtsFuelDetails('CARBON_KG', [fuel, { ...fuel, fuelType: ' biomass ' }]);
    expect(isEtsReportingComplete(updateEtsFuelDetails(payload, duplicates))).toBe(false);
    const tooMany = createEtsFuelDetails(
      'CARBON_KG',
      Array.from({ length: 11 }, (_, index) => ({ ...fuel, fuelType: String(index) })),
    );
    expect(isEtsReportingComplete(updateEtsFuelDetails(payload, tooMany))).toBe(false);
  });

  it.each(['', ' ', '-1', 'Infinity', 'NaN', '0.12345678', 'not a number'])(
    'rejects invalid persisted decimals %j',
    (value) => expect(isValidEtsDecimal(value)).toBe(false),
  );

  it('handles API scientific zeros while enforcing positive delivered values', () => {
    expect(isValidEtsDecimal('0E-7')).toBe(true);
    expect(isValidEtsDecimal('0E-7', true)).toBe(false);
    expect(isValidEtsDecimal('0.0000001', true)).toBe(true);
  });
});
