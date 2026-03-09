import { RequestActionDTO, UnderlyingAgreementVariationActivatedRequestActionPayload } from 'cca-api';

const mockVariationActivatedPayload: UnderlyingAgreementVariationActivatedRequestActionPayload = {
  defaultContacts: [
    {
      name: 'Res name',
      email: 'res@test.com',
      recipientType: 'RESPONSIBLE_PERSON',
    },
    {
      name: 'Fred_2 William_2',
      email: 'fredwilliam_2@agindustries.org.uk',
      recipientType: 'SECTOR_CONTACT',
    },
    {
      name: 'Admin name',
      email: 'admin@test.com',
      recipientType: 'ADMINISTRATIVE_CONTACT',
    },
  ],
  usersInfo: {
    '852901e2-65a1-4e6f-b43b-9c53c1a2ef73': {
      name: 'Regulator name',
    },
  },
  decisionNotification: {
    signatory: '852901e2-65a1-4e6f-b43b-9c53c1a2ef73',
  },
  officialNotices: [
    {
      name: 'Underlying Agreement v3.pdf',
      uuid: 'ad2d77a4-9882-4bcd-bfc1-5f7818a527aa',
    },
  ],
  payloadType: 'UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_PAYLOAD',
  underlyingAgreementActivationDetails: {
    comments: 'comments',
    evidenceFiles: [],
  },
  underlyingAgreementActivationAttachments: {},
  underlyingAgreementDocuments: {
    CCA_2: {
      name: 'Activated underlying agreement cover letter.pdf',
      uuid: 'abcd-1234',
    },
  },
};

const mockRequestAction: RequestActionDTO = {
  id: 64,
  type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED',
  payload: mockVariationActivatedPayload,
  requestId: 'ADS_2-T00026-VAR-1',
  requestType: 'UNDERLYING_AGREEMENT_VARIATION',
  requestAccountId: 45,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator name',
  creationDate: '2024-09-25T18:21:32.751217Z',
};

export const mockRequestActionVariationActivatedState = {
  action: mockRequestAction,
};
