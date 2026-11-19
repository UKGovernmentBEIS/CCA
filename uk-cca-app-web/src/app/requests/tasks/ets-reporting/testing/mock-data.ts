import { ETS_ENERGY_FUEL_DETAILS_SUBTASK, EtsReportingTaskPayload } from '../ets-reporting.types';
import { createEtsFuelDetails, mapEtsStandardFuelRows } from '../ets-reporting-calculations';

export function mockEtsPayload(overrides: Partial<EtsReportingTaskPayload> = {}): EtsReportingTaskPayload {
  return {
    payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_PAYLOAD',
    targetYear: 2026,
    uketsId: 'UK-E-IN-12345',
    measurementType: 'ENERGY_KWH',
    sectionsCompleted: {},
    ...overrides,
  };
}

export function mockCompletedEtsPayload(): EtsReportingTaskPayload {
  return mockEtsPayload({
    emissionsTradingSchemeDataInputDetails: { isUltraSmallEmitter: true, provideFuelDetails: false },
    sectionsCompleted: { [ETS_ENERGY_FUEL_DETAILS_SUBTASK]: 'COMPLETED' },
  });
}

export function mockEtsFuelPayload(): EtsReportingTaskPayload {
  const rows = mapEtsStandardFuelRows('ENERGY_KWH');
  rows.find((row) => row.fuelKey === 'NATURAL_GAS').deliveredEnergy = '1000';
  return mockEtsPayload({
    emissionsTradingSchemeDataInputDetails: {
      isUltraSmallEmitter: false,
      energyFuelDetails: createEtsFuelDetails('ENERGY_KWH', rows),
    },
    sectionsCompleted: { [ETS_ENERGY_FUEL_DETAILS_SUBTASK]: 'IN_PROGRESS', anotherSection: 'COMPLETED' },
  });
}
