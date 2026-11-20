import { EmissionsTradingSchemeDataSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export function mockEtsSubmittedPayload(): EmissionsTradingSchemeDataSubmittedRequestActionPayload {
  return {
    payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMITTED_PAYLOAD',
    details: {
      targetPeriodYear: 2026,
      creationDate: '2027-04-30T10:00:00Z',
      submissionDate: '2027-05-01T14:25:00Z',
    },
    performanceData: {
      measurementType: 'ENERGY_KWH',
      uketsId: 'UK-E-IN-0000',
      isUltraSmallEmitter: false,
      fuels: [
        {
          name: 'Natural gas',
          fixedConversionFactorCode: 'NATURAL_GAS',
          conversionFactor: '0.2500001',
          deliveredEnergy: '1000',
          primaryConversionFactor: '1',
          primaryEnergy: '1000',
        },
        {
          name: 'Zero-carbon fuel',
          conversionFactor: '0',
          deliveredEnergy: '10',
          primaryConversionFactor: '1',
          primaryEnergy: '10',
        },
      ],
    },
    calculatedResults: { totalPrimaryEnergy: '1010', totalCo2Emissions: '0.2500001' },
  };
}

export function mockEtsSubmittedAction(payload = mockEtsSubmittedPayload()): RequestActionDTO {
  return {
    id: 42,
    type: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMITTED',
    requestType: 'EMISSIONS_TRADING_SCHEME_DATA',
    requestId: 'ADS-F00040-ETS-1',
    submitter: 'Sector user',
    creationDate: '2027-05-01T14:25:00Z',
    payload,
  };
}
