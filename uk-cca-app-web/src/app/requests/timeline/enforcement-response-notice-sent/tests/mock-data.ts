import { RequestActionState } from '@netz/common/store';

import { NonComplianceEnforcementResponseNoticeSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const enforcementResponseNoticeSentPayload: NonComplianceEnforcementResponseNoticeSubmittedRequestActionPayload =
  {
    payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMITTED_PAYLOAD',
    enforcementResponseNotice: {
      type: 'PENALTY',
      file: 'uuid-1',
      comments: 'A Martini. Shaken, Not Stirred.',
    },
    nonComplianceAttachments: {
      'uuid-1': 'penalty_notice.pdf',
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

export const enforcementResponseNoticeSentRequestActionDTO: RequestActionDTO = {
  id: 1,
  type: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMITTED',
  payload: enforcementResponseNoticeSentPayload,
  requestId: 'AIC-T0004I-NCOM-01',
  requestType: 'NON_COMPLIANCE',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2024-04-01T14:44:00.000000Z',
};

export const enforcementResponseNoticeSentActionStateMock: RequestActionState = {
  action: enforcementResponseNoticeSentRequestActionDTO,
};
