import {
  BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload,
  BuyOutSurplusFacilityPerformanceDetail,
  BuyOutSurplusFacilityTransactionSummary,
  RequestActionDTO,
} from 'cca-api';

import { HistoricalFacility } from '../historical-facilities';

export const mockAccountAction126 = {
  id: 126,
  type: 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED',
  payload: {
    payloadType: 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD',
    runId: 'BOS-TP8007',
    summary: [
      {
        targetPeriodType: 'TP8',
        chargeType: 'FEE',
        targetPeriodBuyOutCost: '11',
        totalFacilities: 1,
        invoicedBuyOutFee: '275',
        officialNotice: { name: 'CCA080012.pdf', uuid: '00000000-0000-4000-8000-000000000126' },
        dueDate: '2026-10-27',
      },
    ],
    defaultContacts: [
      { name: 'John William', recipientType: 'RESPONSIBLE_PERSON', email: 'williamsj@example.com' },
      { name: 'Matthew Johnson', recipientType: 'ADMINISTRATIVE_CONTACT', email: 'mjohnson@example.com' },
    ],
    facilities: [
      {
        facilityBusinessId: 'ADS_1-F00001',
        siteName: 'test facility 2',
        targetPeriodType: 'TP8',
        performanceDataId: 1,
        performanceDataReportVersion: 3,
        submissionType: 'SECONDARY',
        primaryRulesApplied: false,
        data: {
          performanceOutcome: 'TARGET_NOT_MET',
          buyOutRequired: '100',
          surplusGained: '0',
          surplusUsed: '0',
          previousPaidFees: '825',
          previousSurplusUsed: '25',
          previousSurplusGained: '0',
          chargeType: 'FEE',
          invoicedBuyOutFee: '275',
          schemeTotalSurplus: '25',
        },
        transactionCode: 'CCA080012',
      },
    ],
  },
  requestId: 'ADS_1-T00001-BOS-TP8007',
  requestType: 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING',
  requestAccountId: 2,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2026-09-16T02:48:12Z',
} satisfies RequestActionDTO & { payload: BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload };

export function mockFacility(overrides: Partial<HistoricalFacility> = {}): HistoricalFacility {
  return {
    facilityBusinessId: 'AIC-F00064',
    siteName: 'West London Site',
    targetPeriod: 'TP7',
    version: 1,
    submissionType: 'PRIMARY',
    primaryRulesApplied: true,
    performanceOutcome: 'TARGET_NOT_MET',
    surplusUsed: '12',
    chargeType: 'FEE',
    invoicedBuyOutFee: '202',
    transactionCode: 'CCA070023',
    ...overrides,
  };
}

export function mockApiFacility(facility: HistoricalFacility): BuyOutSurplusFacilityPerformanceDetail {
  return {
    facilityBusinessId: facility.facilityBusinessId,
    siteName: facility.siteName,
    targetPeriodType: facility.targetPeriod,
    performanceDataId: 1,
    performanceDataReportVersion: facility.version,
    submissionType: facility.submissionType,
    primaryRulesApplied: facility.primaryRulesApplied,
    transactionCode: facility.transactionCode,
    data: {
      performanceOutcome: facility.performanceOutcome,
      buyOutRequired: facility.buyOutRequired,
      surplusGained: facility.surplusGained,
      surplusUsed: facility.surplusUsed,
      previousPaidFees: facility.previousPaidFees,
      previousSurplusUsed: facility.previousSurplusUsed,
      previousSurplusGained: facility.previousSurplusGained,
      chargeType: facility.chargeType,
      invoicedBuyOutFee: facility.invoicedBuyOutFee,
      schemeTotalSurplus: facility.schemeTotalSurplus ?? '0',
    },
  };
}

export function mockBuyout(
  overrides: Partial<BuyOutSurplusFacilityTransactionSummary> = {},
): BuyOutSurplusFacilityTransactionSummary {
  return {
    targetPeriodType: 'TP7',
    chargeType: 'FEE',
    targetPeriodBuyOutCost: '37',
    totalFacilities: 50,
    invoicedBuyOutFee: '19900',
    officialNotice: { name: 'CCA070023.pdf', uuid: 'tp7-buyout-notice' },
    dueDate: '2028-07-01',
    ...overrides,
  };
}

export function mockRefund(
  overrides: Partial<BuyOutSurplusFacilityTransactionSummary> = {},
): BuyOutSurplusFacilityTransactionSummary {
  return {
    targetPeriodType: 'TP8',
    chargeType: 'REFUND',
    targetPeriodBuyOutCost: '38',
    totalFacilities: 3,
    invoicedBuyOutFee: '199.2500000',
    officialNotice: { name: 'CCA080024.pdf', uuid: 'tp8-refund-notice' },
    // The backend may supply this, but the refund UI must omit it.
    dueDate: '2029-09-01',
    ...overrides,
  };
}

export function mockAccountPayload(
  overrides: Omit<Partial<BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload>, 'facilities'> & {
    facilities?: HistoricalFacility[];
  } = {},
): BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload {
  const { facilities, ...payloadOverrides } = overrides;
  return {
    payloadType: 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD',
    businessId: 'AIC-T00041',
    runId: 'BOS-TP8004',
    summary: [
      mockRefund(),
      mockBuyout({
        targetPeriodType: 'TP8',
        targetPeriodBuyOutCost: '38',
        officialNotice: { name: 'CCA080003.pdf', uuid: 'tp8-buyout-notice' },
      }),
      mockBuyout(),
    ],
    defaultContacts: [
      { name: 'John William', recipientType: 'RESPONSIBLE_PERSON', email: 'williamsj@example.com' },
      { name: 'Matthew Johnson', recipientType: 'ADMINISTRATIVE_CONTACT', email: 'mjohnson@example.com' },
    ],
    facilities: (
      facilities ?? [
        mockFacility({
          facilityBusinessId: 'AIC-F00065',
          siteName: 'North London Site',
          targetPeriod: 'TP8',
          performanceOutcome: 'TARGET_MET',
          chargeType: 'REFUND',
          transactionCode: 'CCA080024',
          invoicedBuyOutFee: '199.2500000',
          surplusUsed: undefined,
          surplusGained: '0.0000000',
        }),
        mockFacility(),
        mockFacility({ targetPeriod: 'TP8', invoicedBuyOutFee: '1001.1234567', transactionCode: 'CCA080003' }),
        mockFacility({
          facilityBusinessId: 'AIC-F00066',
          siteName: 'East London Site',
          targetPeriod: 'TP9',
          performanceOutcome: 'TARGET_MET',
          chargeType: undefined,
          transactionCode: undefined,
          invoicedBuyOutFee: undefined,
          surplusUsed: undefined,
          surplusGained: '15',
        }),
      ]
    ).map(mockApiFacility),
    ...payloadOverrides,
  };
}

export function mockAccountAction(payload = mockAccountPayload(), id = 3293): RequestActionDTO {
  return {
    id,
    type: 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED',
    requestId: 'AIC-T00041-BOS-TP8004',
    requestAccountId: 41,
    requestType: 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING',
    creationDate: '2027-09-02T14:35:00Z',
    payload,
  };
}
