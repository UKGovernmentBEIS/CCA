import { RequestActionState } from '@netz/common/store';

import { RequestActionDTO } from 'cca-api';

const mockCompletedRequestActionDTO: RequestActionDTO = {
  id: 100,
  type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_COMPLETED',
  payload: {
    decisionNotification: {
      signatory: 'user-1',
      operators: [],
      sectorUsers: [],
    },
    defaultContacts: [
      { name: 'John William', email: 'williamj@abc.com', recipientType: 'RESPONSIBLE_PERSON' },
      { name: 'Matthew Johnson', email: 'mjohnson@def.com', recipientType: 'ADMINISTRATIVE_CONTACT' },
    ],
    usersInfo: {
      'user-1': { name: 'Regulator user' },
    },
    officialNotices: [{ name: 'Variation changes completed cover letter.pdf', uuid: 'file-1' }],
    determination: {
      type: 'ACCEPTED',
      additionalInformation: 'Completed',
      files: [],
    },
    reviewAttachments: {},
  } as any,
  requestId: 'AIC.TD00041-UNA',
  requestType: 'UNDERLYING_AGREEMENT_VARIATION',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator user',
  creationDate: '2027-05-01T15:35:00.000Z',
};

export const mockCompletedRequestActionState: RequestActionState = {
  action: mockCompletedRequestActionDTO,
};
