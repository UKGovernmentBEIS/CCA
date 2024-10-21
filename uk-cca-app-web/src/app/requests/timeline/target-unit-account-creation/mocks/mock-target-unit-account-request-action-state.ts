import { RequestActionState } from '@netz/common/store';

import {
  RequestActionDTO,
  TargetUnitAccountCreationSubmittedRequestActionPayload,
  TargetUnitAccountPayload,
} from 'cca-api';

export const mockTargetUnitAccountPayload: TargetUnitAccountPayload = {
  name: 'iojasdoiajsdoijas',
  emissionTradingScheme: 'DUMMY_EMISSION_TRADING_SCHEME',
  competentAuthority: 'ENGLAND',
  operatorType: 'LIMITED_COMPANY',
  isCompanyRegistrationNumber: true,
  companyRegistrationNumber: '123456789',
  address: {
    line1: 'kjhn',
    city: 'kjhn',
    postcode: '87678',
    country: 'AL',
  },
  responsiblePerson: {
    email: 'test-test@cca.uk',
    firstName: 'asdasdsa',
    lastName: 'lname',
    jobTitle: 'job',
    address: {
      line1: 'kjhn',
      city: 'kjhn',
      postcode: '87678',
      country: 'AL',
    },
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
  },
  administrativeContactDetails: {
    email: 'test-admin@test.com',
    firstName: 'asd',
    lastName: 'England',
    jobTitle: 'job1',
    address: {
      line1: 'kjhn',
      city: 'kjhn',
      postcode: '87678',
      country: 'AL',
    },
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
  },
};

export const mockTargetUnitAccountCreationSubmittedRequestActionPayload: TargetUnitAccountCreationSubmittedRequestActionPayload =
  {
    payloadType: 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED_PAYLOAD',
    businessId: 'ADS_2-T00002',
    payload: mockTargetUnitAccountPayload,
  };

export const mockTargetUnitAccountRequestActionDTO: RequestActionDTO = {
  id: 13,
  type: 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED',
  payload: mockTargetUnitAccountCreationSubmittedRequestActionPayload,
  requestId: 'ADS_2-T00002-ACC',
  requestType: 'TARGET_UNIT_ACCOUNT_CREATION',
  requestAccountId: 4,
  competentAuthority: 'ENGLAND',
  submitter: 'fname lname',
  creationDate: '2024-08-30T11:42:04.135076Z',
};

export const mockTargetUnitAccountRequestActionState: RequestActionState = {
  action: mockTargetUnitAccountRequestActionDTO,
};
