import { RequestActionState } from '@netz/common/store';

import { NonComplianceAppealDetailsSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const nonComplianceAppealProvidedPayload: NonComplianceAppealDetailsSubmittedRequestActionPayload = {
  payloadType: 'NON_COMPLIANCE_APPEAL_DETAILS_SUBMITTED_PAYLOAD',
  registrationDate: '2025-03-02',
  files: ['uuid-1'],
  comments: 'A Martini. Shaken, Not Stirred.',
  nonComplianceAttachments: {
    'uuid-1': 'Appeal_file.pdf',
  },
};

export const nonComplianceAppealProvidedRequestActionDTO: RequestActionDTO = {
  id: 1,
  type: 'NON_COMPLIANCE_APPEAL_DETAILS_SUBMITTED',
  payload: nonComplianceAppealProvidedPayload,
  requestId: 'AIC-T00041-NCOM-01',
  requestType: 'NON_COMPLIANCE',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-03-03T11:15:00.000000Z',
};

export const nonComplianceAppealProvidedActionStateMock: RequestActionState = {
  action: nonComplianceAppealProvidedRequestActionDTO,
};
