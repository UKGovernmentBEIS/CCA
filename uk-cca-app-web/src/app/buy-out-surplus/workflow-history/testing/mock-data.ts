import { ItemDTOResponse, RequestActionInfoDTO, RequestDetailsDTO } from 'cca-api';

export const mockWorkflowDetails: RequestDetailsDTO = {
  id: 'BOS-TP6003',
  requestType: 'BUY_OUT_SURPLUS_RUN',
  requestStatus: 'COMPLETED',
  creationDate: '2025-04-09',
  requestMetadata: {
    type: 'BUY_OUT_SURPLUS_RUN',
    targetPeriodType: 'TP6',
    totalAccounts: 1,
    failedAccounts: 0,
  } as any,
};

export const mockEmptyRequestItems: ItemDTOResponse = {
  items: [],
  totalItems: 0,
};

export const mockRequestActions: RequestActionInfoDTO[] = [
  {
    id: 48,
    type: 'BUY_OUT_SURPLUS_RUN_COMPLETED',
    submitter: 'Regulator England',
    creationDate: '2025-04-09T11:36:29.5858Z',
  },
  {
    id: 46,
    type: 'BUY_OUT_SURPLUS_RUN_SUBMITTED',
    submitter: 'Regulator England',
    creationDate: '2025-04-09T11:36:24.703976Z',
  },
];
