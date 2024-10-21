import { RequestActionState } from '@netz/common/store';

import { AdminTerminationFinalDecisionSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const mockAdminTerminationFinalDecisionSubmittedRequestActionPayload: AdminTerminationFinalDecisionSubmittedRequestActionPayload =
  {
    payloadType: 'ADMIN_TERMINATION_FINAL_DECISION_SUBMITTED_PAYLOAD',
    adminTerminationFinalDecisionReasonDetails: {
      finalDecisionType: 'TERMINATE_AGREEMENT',
      explanation: 'asdsadas',
      relevantFiles: [],
    },
    decisionNotification: {
      signatory: '6806104f-dca8-41c5-b325-b138e423c489',
    },
    adminTerminationFinalDecisionAttachments: {},
    usersInfo: {
      '6806104f-dca8-41c5-b325-b138e423c489': {
        name: 'Regulator England',
      },
    },
    defaultContacts: [
      {
        name: 'oper3 user',
        email: 'oper3@cca.uk',
        recipientType: 'RESPONSIBLE_PERSON',
      },
      {
        name: 'a-fname lname',
        email: 'test-admin@test.com',
        recipientType: 'ADMINISTRATIVE_CONTACT',
      },
      {
        name: 'Fred_1 William_1',
        email: 'fredwilliam_1@agindustries.org.uk',
        recipientType: 'SECTOR_CONTACT',
      },
    ],
    officialNotice: {
      name: 'Admin Termination Regulatory reason notice.pdf',
      uuid: '3afc5391-4957-4105-9770-bfcb68de9d17',
    },
  };

export const mockAdminTerminationFinalDecisionSubmittedRequestActionDTO: RequestActionDTO = {
  id: 11,
  type: 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED',
  payload: mockAdminTerminationFinalDecisionSubmittedRequestActionPayload,
  requestId: 'ADS_1-T00002-ATER-3',
  requestType: 'ADMIN_TERMINATION',
  requestAccountId: 3,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2024-08-28T16:31:14.409801Z',
};

export const mockAdminTerminationFinalDecisionRequestActionState: RequestActionState = {
  action: mockAdminTerminationFinalDecisionSubmittedRequestActionDTO,
};
