import { BuyoutSurplusState } from '../buy-out-surplus.store';

export const buyoutSurplusStateMockData: BuyoutSurplusState = {
  currentPage: 0,
  runInProgress: true,
  badgeNumber: 1,
  pageSize: 0,
  workflowsHistory: [
    {
      id: 'BOS-TP6002',
      requestType: 'BUY_OUT_SURPLUS_RUN',
      requestStatus: 'IN_PROGRESS',
      creationDate: '2025-04-08',
      requestMetadata: {
        type: 'BUY_OUT_SURPLUS_RUN',
        targetPeriodType: 'TP6',
        totalAccounts: 2,
        failedAccounts: 1,
      } as any,
    },
    {
      id: 'BS-TP6001',
      requestType: 'BUY_OUT_SURPLUS_RUN',
      requestStatus: 'COMPLETED',
      creationDate: '2025-04-03',
      requestMetadata: {
        type: 'BUY_OUT_SURPLUS_RUN',
        targetPeriodType: 'TP6',
        totalAccounts: 2,
        failedAccounts: 1,
      },
    },
  ],
  totalWorkflowHistoryItems: 2,
};
