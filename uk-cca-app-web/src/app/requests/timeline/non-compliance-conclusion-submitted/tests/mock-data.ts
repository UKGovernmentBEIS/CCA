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

export const nonComplianceConclusionWithdrawSubmittedPayload: NonComplianceConclusionSubmittedRequestActionPayload = {
  payloadType: 'NON_COMPLIANCE_CONCLUSION_SUBMITTED_PAYLOAD',
  nonComplianceConclusion: {
    details: {
      complianceRestored: true,
      complianceRestoredDate: '2025-03-02',
      penaltyPaid: true,
      penaltyPaymentDate: '2025-03-02',
      comment: 'A Martini. Shaken, Not Stirred.',
      penaltyOutcome: 'WITHDRAW',
    },
    withdrawNotice: {
      file: 'uuid-1',
      comments: 'Withdrawal notice comments',
    },
  },
  nonComplianceAttachments: {
    'uuid-1': 'withdrawal-notice.pdf',
  },
  decisionNotification: {
    operators: ['operator-user-id'],
    signatory: 'regulator-user-id',
  },
  usersInfo: {
    'operator-user-id': {
      name: 'Alex Turner',
    },
    'regulator-user-id': {
      name: 'Regulator England',
    },
  },
  defaultContacts: [
    {
      name: 'John William',
      email: 'williamsj@abc.com',
      recipientType: 'RESPONSIBLE_PERSON',
    },
    {
      name: 'Matthew Johnson',
      email: 'mjohnson@def.com',
      recipientType: 'ADMINISTRATIVE_CONTACT',
    },
  ],
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
