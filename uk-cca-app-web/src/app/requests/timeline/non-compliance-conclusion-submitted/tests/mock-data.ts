import { RequestActionState } from '@netz/common/store';

import { NonComplianceConclusionSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const nonComplianceConclusionSubmittedPayload: NonComplianceConclusionSubmittedRequestActionPayload = {
  payloadType: 'NON_COMPLIANCE_CONCLUSION_SUBMITTED_PAYLOAD',
  nonComplianceConclusion: {
    details: {
      complianceRestored: true,
      complianceRestoredDate: '2025-03-02',
      penaltyPaid: true,
      penaltyPaymentDate: '2025-03-02',
      comment: 'A Martini. Shaken, Not Stirred.',
      penaltyOutcome: 'NONE',
    },
  },
  nonComplianceAttachments: {},
};

export const nonComplianceConclusionSubmittedRequestActionDTO: RequestActionDTO = {
  id: 1,
  type: 'NON_COMPLIANCE_CONCLUSION_SUBMITTED',
  payload: nonComplianceConclusionSubmittedPayload,
  requestId: 'AIC-T00041-NCOM-01',
  requestType: 'NON_COMPLIANCE',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-03-04T09:21:00.000000Z',
};

export const nonComplianceConclusionSubmittedActionStateMock: RequestActionState = {
  action: nonComplianceConclusionSubmittedRequestActionDTO,
};
