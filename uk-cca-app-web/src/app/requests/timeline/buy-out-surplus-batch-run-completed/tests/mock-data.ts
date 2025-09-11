import { RequestActionState } from '@netz/common/store';

import { BuyOutSurplusRunCompletedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const buyoutSurplusBatchRunCompletedMock: BuyOutSurplusRunCompletedRequestActionPayload = {
  payloadType: 'BUY_OUT_SURPLUS_RUN_COMPLETED_PAYLOAD',
  runSummary: {
    totalAccounts: 1,
    failedAccounts: 0,
    buyOutTransactions: 1,
    refundedTransactions: 0,
  },
  csvFile: {
    name: 'BOS-TP6003 Buy-out and surplus summary report.csv',
    uuid: '650c6835-dbf3-4fd2-a0d6-7ac1e8919234',
  },
};

export const buyoutSurplusBatchRunCompletedRequestActionDTO: RequestActionDTO = {
  id: 48,
  type: 'BUY_OUT_SURPLUS_RUN_COMPLETED',
  payload: buyoutSurplusBatchRunCompletedMock,
  requestId: 'BOS-TP6003',
  requestType: 'BUY_OUT_SURPLUS_RUN',
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-04-09T11:36:29.5858Z',
};

export const buyoutSurplusBatchRunCompletedActionStateMock: RequestActionState = {
  action: buyoutSurplusBatchRunCompletedRequestActionDTO,
};
