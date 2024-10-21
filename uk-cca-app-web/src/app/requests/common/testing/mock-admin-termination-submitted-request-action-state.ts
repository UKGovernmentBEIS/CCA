import { RequestActionState } from '@netz/common/store';

import { AdminTerminationSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const mockAdminTerminationSubmittedRequestActionPayload: AdminTerminationSubmittedRequestActionPayload = {
  payloadType: 'ADMIN_TERMINATION_SUBMITTED_PAYLOAD',
  adminTerminationReasonDetails: {
    reason: 'FAILURE_TO_AGREE',
    explanation: 'asdsadsad',
    relevantFiles: ['c2b2b4cf-9d53-4225-a8ae-d1a4f9c2fad4'],
  },
  decisionNotification: {
    signatory: '6806104f-dca8-41c5-b325-b138e423c489',
  },
  adminTerminationSubmitAttachments: {
    'c2b2b4cf-9d53-4225-a8ae-d1a4f9c2fad4': 'sample_profile1.png',
  },
  usersInfo: {
    '6806104f-dca8-41c5-b325-b138e423c489': {
      name: 'Regulator England',
    },
  },
  defaultContacts: [
    {
      name: 'asdasdsa lname',
      email: 'test-test@cca.uk',
      recipientType: 'RESPONSIBLE_PERSON',
    },
    {
      name: 'asd England',
      email: 'test-admin@test.com',
      recipientType: 'ADMINISTRATIVE_CONTACT',
    },
    {
      name: 'Fred_2 William_2',
      email: 'fredwilliam_2@agindustries.org.uk',
      recipientType: 'SECTOR_CONTACT',
    },
  ],
  officialNotice: {
    name: 'Notice of intent to terminate agreement.pdf',
    uuid: '5c8f509c-1f8f-4b8a-b183-66351193860b',
  },
};

export const mockAdminTerminationSubmittedRequestActionDTO: RequestActionDTO = {
  id: 14,
  type: 'ADMIN_TERMINATION_APPLICATION_SUBMITTED',
  payload: mockAdminTerminationSubmittedRequestActionPayload,
  requestId: 'ADS_2-T00002-ATER-1',
  requestType: 'ADMIN_TERMINATION',
  requestAccountId: 4,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2024-08-30T11:44:57.536474Z',
};

export const mockAdminTerminationSubmittedRequestActionState: RequestActionState = {
  action: mockAdminTerminationSubmittedRequestActionDTO,
};
