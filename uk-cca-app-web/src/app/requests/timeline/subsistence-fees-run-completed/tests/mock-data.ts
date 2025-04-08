import { RequestActionState } from '@netz/common/store';

import { RequestActionDTO, SubsistenceFeesRunCompletedRequestActionPayload } from 'cca-api';

export const subsistenceFeesRunCompletedMock: SubsistenceFeesRunCompletedRequestActionPayload = {
  paymentRequestId: 'S2501',
  chargingYear: '2025',
  status: 'COMPLETED',
  sentInvoices: 1,
  failedInvoices: 0,
  report: {
    name: 'S2501 subsistence fees summary report.csv',
    uuid: '7867ac74-ca50-4d79-9b70-513d286caa46',
  },
} as any; // This is to bypass `chargingYear` type of `SubsistenceFeesRunCompletedRequestActionPayloadChargingYear`

export const subsistenceFeesRunCompletedRequestActionDTO: RequestActionDTO = {
  id: 6,
  type: 'SUBSISTENCE_FEES_RUN_COMPLETED',
  payload: subsistenceFeesRunCompletedMock,
  requestId: 'S2501',
  requestType: 'SUBSISTENCE_FEES_RUN',
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-02-11T13:17:35.149924Z',
};

export const subsistenceFeesRunCompletedActionStateMock: RequestActionState = {
  action: subsistenceFeesRunCompletedRequestActionDTO,
};
