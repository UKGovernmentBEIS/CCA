import { DefaultNoticeRecipient, RequestActionDTO, UnderlyingAgreementActivatedRequestActionPayload } from 'cca-api';

const defaultContacts: Array<DefaultNoticeRecipient> = [
  {
    name: 'test test',
    email: 'test@test.com',
    recipientType: 'RESPONSIBLE_PERSON',
  },
  {
    name: 'Fred_2 William_2',
    email: 'fredwilliam_2@agindustries.org.uk',
    recipientType: 'SECTOR_CONTACT',
  },
  {
    name: 'test test',
    email: 'test@test.com',
    recipientType: 'ADMINISTRATIVE_CONTACT',
  },
];

const usersInfo = {
  '852901e2-65a1-4e6f-b43b-9c53c1a2ef73': {
    name: 'Regulator 2',
  },
};

const decisionNotification = {
  signatory: '852901e2-65a1-4e6f-b43b-9c53c1a2ef73',
};

const officialNotice = {
  name: 'Underlying agreement rejection notice.pdf',
  uuid: 'ad2d77a4-9882-4bcd-bfc1-5f7818a527aa',
};

const activatedPayloadDTO: UnderlyingAgreementActivatedRequestActionPayload = {
  determination: null,
  underlyingAgreement: null,
  ...{
    defaultContacts: defaultContacts,
    usersInfo: usersInfo,
    decisionNotification: decisionNotification,
    officialNotice: officialNotice,
  },
  payloadType: 'UNDERLYING_AGREEMENT_ACTIVATED_PAYLOAD',
  underlyingAgreementActivationDetails: {
    comments: 'comments',
    evidenceFiles: [],
  },
  underlyingAgreementActivationAttachments: {},
  underlyingAgreementDocuments: {
    CCA_2: {
      name: 'underlying-agreement.pdf',
      uuid: 'abcd-1234',
    },
  },
};

const mockRequestActivatedActionDTO: RequestActionDTO = {
  id: 64,
  type: 'UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED',
  payload: activatedPayloadDTO,
  requestId: 'ADS_2-T00026-UNA',
  requestType: 'UNDERLYING_AGREEMENT',
  requestAccountId: 45,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator 2',
  creationDate: '2024-09-25T18:21:32.751217Z',
};

export const mockRequestActionActivatedState = {
  action: mockRequestActivatedActionDTO,
};
