import { RequestActionState } from '@netz/common/store';

import { NonComplianceNoticeOfIntentSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const nonComplianceNoticeOfIntentSubmittedPayload: NonComplianceNoticeOfIntentSubmittedRequestActionPayload = {
  payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMITTED_PAYLOAD',
  noticeOfIntent: {
    file: 'uuid-1',
    comments: 'A Martini. Shaken, Not Stirred.',
  },
  nonComplianceAttachments: {
    'uuid-1': 'filename.xls',
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

export const nonComplianceNoticeOfIntentSubmittedRequestActionDTO: RequestActionDTO = {
  id: 1,
  type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMITTED',
  payload: nonComplianceNoticeOfIntentSubmittedPayload,
  requestId: 'AIC-T0004I-NCOM-01',
  requestType: 'NON_COMPLIANCE',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2024-04-01T14:44:00.000000Z',
};

export const nonComplianceNoticeOfIntentSubmittedActionStateMock: RequestActionState = {
  action: nonComplianceNoticeOfIntentSubmittedRequestActionDTO,
};
