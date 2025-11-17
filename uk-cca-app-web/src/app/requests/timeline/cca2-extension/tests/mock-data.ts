import { RequestActionState } from '@netz/common/store';

import { Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const cca2ExtensionPayload: Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload = {
  payloadType: 'CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD',
  officialNotice: {
    name: 'CCA2 Extension Activated underlying agreement cover letter.pdf',
    uuid: '701d9772-0837-4858-b681-b803ad660bf7',
  },
  underlyingAgreementDocument: {
    name: 'ADS_1-T00001 CCA2 Underlying Agreement v2.pdf',
    uuid: '40ca0b9a-82a6-41b8-a2eb-a944a94e93d4',
  },
  defaultContacts: [
    {
      name: 'resp1 user',
      email: 'resp1@cca.uk',
      recipientType: 'RESPONSIBLE_PERSON',
    },
    {
      name: 'administr1 user',
      email: 'administr1@cca.uk',
      recipientType: 'ADMINISTRATIVE_CONTACT',
    },
    {
      name: 'Fred_1 William_1',
      email: 'fredwilliam_1@agindustries.org.uk',
      recipientType: 'SECTOR_CONTACT',
    },
  ],
};

export const cca2ExtensionRequestActionDTO: RequestActionDTO = {
  id: 121,
  type: 'CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED',
  payload: cca2ExtensionPayload,
  requestId: 'ADS_1-T00001-CCA2-EXT-1',
  requestType: 'CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  creationDate: '2025-10-29T14:52:24.845314Z',
};

export const cca2ExtensionActionStateMock: RequestActionState = {
  action: cca2ExtensionRequestActionDTO,
};
