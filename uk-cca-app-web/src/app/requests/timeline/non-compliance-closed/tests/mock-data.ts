import { RequestActionState } from '@netz/common/store';

import { NonComplianceClosedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const nonComplianceClosedPayload: NonComplianceClosedRequestActionPayload = {
  payloadType: 'NON_COMPLIANCE_CLOSED_PAYLOAD',
  businessId: 'AIC-T00041-NCOM-01',
  reason: 'There is nothing left to complete.',
  files: ['uuid-1'],
  nonComplianceAttachments: {
    'uuid-1': 'close-task.pdf',
  },
};

export const nonComplianceClosedRequestActionDTO: RequestActionDTO = {
  id: 1,
  type: 'NON_COMPLIANCE_CLOSED',
  payload: nonComplianceClosedPayload,
  requestId: 'AIC-T00041-NCOM-01',
  requestType: 'NON_COMPLIANCE',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-03-03T11:15:00.000000Z',
};

export const nonComplianceClosedActionStateMock: RequestActionState = {
  action: nonComplianceClosedRequestActionDTO,
};
