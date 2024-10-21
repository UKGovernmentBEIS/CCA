import { RequestActionState } from '@netz/common/store';

import { AdminTerminationWithdrawSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const mockAdminTerminationWithdrawSubmittedRequestActionPayload: AdminTerminationWithdrawSubmittedRequestActionPayload =
  {
    payloadType: 'ADMIN_TERMINATION_WITHDRAW_SUBMITTED_PAYLOAD',
    adminTerminationWithdrawReasonDetails: {
      explanation: 'erhserseresrg',
      relevantFiles: ['4e60d4e4-42ca-427c-8105-a1f12b310d32'],
    },
    decisionNotification: {
      signatory: '6806104f-dca8-41c5-b325-b138e423c489',
    },
    adminTerminationWithdrawAttachments: {
      '4e60d4e4-42ca-427c-8105-a1f12b310d32': 'METS Project Retro.xlsx',
    },
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
      name: 'Withdrawal of intent to terminate agreement.pdf',
      uuid: '080d0102-4782-482d-8a6c-dff764ceedf8',
    },
  };

export const mockAdminTerminationWithdrawSubmittedRequestActionDTO: RequestActionDTO = {
  id: 9,
  type: 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED',
  payload: mockAdminTerminationWithdrawSubmittedRequestActionPayload,
  requestId: 'ADS_1-T00002-ATER-2',
  requestType: 'ADMIN_TERMINATION',
  requestAccountId: 3,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2024-08-27T12:35:12.07002Z',
};

export const mockAdminTerminationWithdrawSubmittedRequestActionState: RequestActionState = {
  action: mockAdminTerminationWithdrawSubmittedRequestActionDTO,
};
