import { RequestActionState } from '@netz/common/store';

import { NonComplianceAppealOutcomeSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const appealOutcomeSubmittedPayload: NonComplianceAppealOutcomeSubmittedRequestActionPayload = {
  payloadType: 'NON_COMPLIANCE_APPEAL_OUTCOME_SUBMITTED_PAYLOAD',
  appealOutcome: {
    tribunalDecision: 'APPEAL_ALLOWED',
    appealOutcomeDate: '2025-03-02',
    file: 'uuid-1',
    comments: 'A Martini. Shaken, Not Stirred.',
  },
  nonComplianceAttachments: {
    'uuid-1': 'Appeal_outcome_file.pdf',
  },
};

export const appealOutcomeSubmittedRequestActionDTO: RequestActionDTO = {
  id: 1,
  type: 'NON_COMPLIANCE_APPEAL_OUTCOME_SUBMITTED',
  payload: appealOutcomeSubmittedPayload,
  requestId: 'AIC-T00041-NCOM-01',
  requestType: 'NON_COMPLIANCE',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-03-04T09:21:00.000000Z',
};

export const appealOutcomeSubmittedActionStateMock: RequestActionState = {
  action: appealOutcomeSubmittedRequestActionDTO,
};
