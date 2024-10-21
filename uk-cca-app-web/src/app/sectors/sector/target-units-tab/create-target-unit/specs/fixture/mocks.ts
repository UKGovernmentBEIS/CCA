import { TargetUnitAccountPayload } from 'cca-api';

export const mockCountries = {
  COUNTRIES: [
    {
      code: 'PT',
      name: 'Portugal',
      officialName: 'The Portuguese Republic',
    },
    {
      code: 'PW',
      name: 'Palau',
      officialName: 'The Republic of Palau',
    },
    {
      code: 'GB',
      name: 'United Kingdom',
      officialName: 'United Kingdom',
    },
  ],
};

export const mockCounties = {
  COUNTIES: [
    {
      id: 1,
      name: 'Portugal',
    },
    {
      id: 2,
      name: 'Palau',
    },
    {
      id: 3,
      name: 'United Kingdom',
    },
  ],
};

export const mockCreateTargetUnitState: TargetUnitAccountPayload = {
  name: 'Operator name',
  emissionTradingScheme: 'DUMMY_EMISSION_TRADING_SCHEME',
  competentAuthority: 'ENGLAND',
  operatorType: 'LIMITED_COMPANY',
  companyRegistrationNumber: '12345',
  registrationNumberMissingReason: null,
  sicCode: '54321',
  subsectorAssociationId: 150,
  address: {
    city: 'City',
    country: 'UK',
    line1: 'Address line 1',
    line2: 'Address line 2',
    postcode: '56789',
  },
  responsiblePerson: {
    address: {
      city: 'City pers',
      country: 'UK',
      line1: 'Address line 1',
      line2: 'Address line 2',
      postcode: '56789',
    },
    email: 'resp@test.com',
    firstName: 'Resp fname',
    lastName: 'Resp lname',
    jobTitle: 'resp job',
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
  },
  administrativeContactDetails: {
    address: {
      city: 'City admin',
      country: 'UK',
      line1: 'Address line 1',
      line2: 'Address line 2',
      postcode: '56789',
    },
    email: 'admin@test.com',
    firstName: 'Admin fname',
    lastName: 'Admin lname',
    jobTitle: 'admin job',
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
  },
  isCompanyRegistrationNumber: true,
};
