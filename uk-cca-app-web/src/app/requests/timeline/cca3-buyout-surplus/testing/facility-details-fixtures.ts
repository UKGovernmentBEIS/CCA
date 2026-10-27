import { FacilityCalculationPresentation } from '../facility-details/facility-calculation.types';
import { HistoricalFacilityDetails } from '../facility-details/facility-details.resolver';
import { HistoricalFacility } from '../historical-facilities';

export function mockHistoricalFacility(overrides: Partial<HistoricalFacility> = {}): HistoricalFacilityDetails {
  return {
    actionId: 3293,
    creationDate: '2027-09-02T14:35:00Z',
    runId: 'BOS-TP8004',
    facility: {
      facilityBusinessId: 'AIC-F00064',
      siteName: 'West London Site',
      targetPeriod: 'TP7',
      version: 3,
      submissionType: 'PRIMARY',
      primaryRulesApplied: true,
      surplusUsed: '0',
      surplusGained: '0',
      previousSurplusUsed: '0',
      previousSurplusGained: '0',
      previousPaidFees: '0',
      performanceOutcome: 'TARGET_NOT_MET',
      buyOutRequired: '40',
      buyOutCost: '37',
      transactionCode: 'CCA070021',
      ...overrides,
    },
  };
}

export const facilityDetailsFixtures: {
  node: string;
  details: HistoricalFacilityDetails;
  calculation: FacilityCalculationPresentation;
}[] = [
  {
    node: '35065-12236',
    details: {
      ...mockHistoricalFacility({ performanceOutcome: 'TARGET_MET', version: 1, surplusGained: '50' }),
      creationDate: '2027-05-02T14:35:00Z',
    },
    calculation: { scenario: 'primary-surplus' },
  },
  {
    node: '35073-39254',
    details: mockHistoricalFacility({ surplusUsed: '10', invoicedBuyOutFee: '1110', chargeType: 'FEE' }),
    calculation: { scenario: 'primary-buyout', showTransaction: true },
  },
  {
    node: '35073-38970',
    details: mockHistoricalFacility({ surplusUsed: '40', invoicedBuyOutFee: '0', transactionCode: undefined }),
    calculation: { scenario: 'primary-buyout', showTransaction: false },
  },
  {
    node: '35065-37575',
    details: mockHistoricalFacility({
      submissionType: 'SECONDARY',
      primaryRulesApplied: false,
      performanceOutcome: 'TARGET_MET',
      surplusGained: '60',
      previousSurplusGained: '50',
    }),
    calculation: { scenario: 'secondary-surplus' },
  },
  {
    node: '35065-38309',
    details: mockHistoricalFacility({
      submissionType: 'SECONDARY',
      primaryRulesApplied: false,
      performanceOutcome: 'TARGET_MET',
      surplusGained: '100',
      previousPaidFees: '1000',
      previousSurplusUsed: '0',
      invoicedBuyOutFee: '1000',
      chargeType: 'REFUND',
    }),
    calculation: { scenario: 'secondary-target-met-refund' },
  },
  {
    node: '35065-37231',
    details: mockHistoricalFacility({
      submissionType: 'SECONDARY',
      primaryRulesApplied: false,
      targetPeriod: 'TP8',
      buyOutCost: '38',
      previousPaidFees: '520',
      previousSurplusUsed: '10',
      invoicedBuyOutFee: '1000',
      chargeType: 'FEE',
    }),
    calculation: { scenario: 'secondary-buyout-after-buyout', grossBuyoutFee: '1520' },
  },
  {
    node: '35505-25783',
    details: mockHistoricalFacility({
      submissionType: 'SECONDARY',
      primaryRulesApplied: false,
      previousSurplusGained: '10',
      invoicedBuyOutFee: '1480',
      chargeType: 'FEE',
    }),
    calculation: { scenario: 'secondary-buyout-after-surplus', grossBuyoutFee: '1480' },
  },
  {
    node: '35065-37898',
    details: mockHistoricalFacility({
      submissionType: 'SECONDARY',
      primaryRulesApplied: false,
      previousPaidFees: '1800',
      previousSurplusUsed: '0',
      invoicedBuyOutFee: '320',
      chargeType: 'REFUND',
    }),
    calculation: { scenario: 'secondary-target-not-met-refund', grossBuyoutFee: '1480' },
  },
];
