import { RequestActionState } from '@netz/common/store';

import { RequestActionDTO } from 'cca-api';

// BuyOutSurplusFacilityRunCompletedRequestActionPayload
export const buyOutSurplusFacilityBatchRunCompletedMock = {
  payloadType: 'BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED_PAYLOAD',
  runSummary: {
    totalAccounts: 3000,
    failedAccounts: 1700,
    totalFacilities: 11000,
    failedFacilities: 3500,
    runSummaryEntries: [
      {
        targetPeriod: 'TP8',
        buyOutCost: 38,
        totalFacilities: 5500,
        totalBuyOutTransactions: 1700,
        totalBuyOutFacilities: 4000,
        totalRefundTransactions: 2,
        totalRefundFacilities: 2,
      },
      {
        targetPeriod: 'TP7',
        buyOutCost: 37,
        totalFacilities: 5500,
        totalBuyOutTransactions: 1700,
        totalBuyOutFacilities: 4000,
        totalRefundTransactions: 2,
        totalRefundFacilities: 2,
      },
    ],
  },
  csvFile: {
    name: 'BOS-TP8001 buy-out and surplus summary report.csv',
    uuid: '650c6835-dbf3-4fd2-a0d6-7ac1e8919234',
  },
};

export const buyOutSurplusFacilityBatchRunCompletedRequestActionDTO: RequestActionDTO = {
  id: 49,
  type: 'BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED',
  payload: buyOutSurplusFacilityBatchRunCompletedMock,
  requestId: 'BOS-TP8001',
  requestType: 'BUY_OUT_SURPLUS_FACILITY_RUN',
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2029-05-03T15:35:00.0000Z',
};

export const buyOutSurplusFacilityBatchRunCompletedActionStateMock: RequestActionState = {
  action: buyOutSurplusFacilityBatchRunCompletedRequestActionDTO,
};

export const buyOutSurplusFacilityBatchRunCompletedWithFailuresActionStateMock: RequestActionState = {
  action: {
    ...buyOutSurplusFacilityBatchRunCompletedRequestActionDTO,
    type: 'BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED_WITH_FAILURES',
  },
};
