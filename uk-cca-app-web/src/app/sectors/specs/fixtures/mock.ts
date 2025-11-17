import { AuthState } from '@netz/common/auth';

import {
  CcaOperatorUserDetailsDTO,
  OperatorAuthoritiesInfoDTO,
  SectorAssociationResponseDTO,
  SectorAssociationSchemesDTO,
  SectorUserAuthorityDetailsDTO,
  SubsectorAssociationSchemesDTO,
  TargetUnitAccountDetailsDTO,
  TargetUnitAccountInfoResponseDTO,
  UnderlyingAgreementDetailsDTO,
} from 'cca-api';

export const mockSectors = [
  {
    id: 1,
    sector: 'ADS - Aerospace',
    mainContact: 'Fred William',
  },
  {
    id: 2,
    sector: 'ADS1 - Aerospace1',
    mainContact: 'Andrew William',
  },
  {
    id: 3,
    sector: 'ADS2 - Aerospace2',
    mainContact: 'Maria William',
  },
  {
    id: 4,
    sector: 'ADS3 - Aerospace3',
    mainContact: 'Tina William',
  },
];

export const mockSectorDetails: SectorAssociationResponseDTO = {
  editable: true,
  sectorAssociationContact: {
    title: 'Mr.',
    jobTitle: 'job title',
    organisationName: 'org name',
    phoneNumber: '123456789',
    firstName: 'John',
    lastName: 'Doe',
    address: {
      line1: 'address 1',
      line2: '',
      city: 'city 1',
      county: '',
      postcode: '12345',
    },
    email: 'johny@doe.com',
  },
  sectorAssociationDetails: {
    acronym: 'acronym',
    legalName: 'legal',
    facilitator: {
      firstName: 'facilitator name',
      lastName: 'facilitator last name',
    },
    noticeServiceAddress: {
      line1: 'address 1',
      line2: '',
      city: 'city 1',
      county: '',
      postcode: '12345',
    },
    competentAuthority: 'ENGLAND',
    commonName: 'common',
    energyIntensiveOrEPR: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
  },
};

export const mockSectorScheme: SectorAssociationSchemesDTO = {
  sectorAssociationSchemeMap: {
    ['CCA_2']: {
      id: 0,
      umbrellaAgreement: {
        uuid: 'uuid-ambrella',
        id: 1,
        fileName: 'file-name',
        fileSize: 20,
        fileType: 'pdf',
      },
      targetSet: {
        id: 0,
        targetCurrencyType: 'NOVEM',
        energyOrCarbonUnit: 'kWh',
        targetCommitments: [
          {
            targetImprovement: '0.10',
            targetPeriod: '2010-2011',
          },
          {
            targetImprovement: '0.15',
            targetPeriod: '2020-2021',
          },
        ],
      },
    },
  },
  subsectorAssociations: [
    {
      id: 1,
      name: 'sub-name-1',
    },
    {
      id: 2,
      name: 'sub-name-2',
    },
  ],
};

export const mockSubSectorDetails: SubsectorAssociationSchemesDTO = {
  name: 'sub-sector-name',
  subsectorAssociationSchemeMap: {
    ['CCA_2']: {
      targetSet: {
        id: 0,
        targetCurrencyType: 'Relative',
        throughputUnit: 'tonne',
        energyOrCarbonUnit: 'kWh',
        targetCommitments: [
          {
            targetImprovement: '0.10',
            targetPeriod: '2010-2011',
          },
          {
            targetImprovement: '0.15',
            targetPeriod: '2020-2021',
          },
        ],
      },
    },
  },
};

export const mockSectorUserDetails: SectorUserAuthorityDetailsDTO = {
  email: 'sector-ser@cca.uk',
  firstName: 'sector',
  lastName: 'user',
  status: 'REGISTERED',
  phoneNumber: {
    countryCode: '0044',
    number: '1234567890',
  },
  mobileNumber: {
    countryCode: '0044',
    number: '1234567890',
  },
  contactType: 'SECTOR_ASSOCIATION',
  organisationName: 'org',
};

export const mockSectorAuthorities = {
  authorities: [
    {
      firstName: 'Sector 2',
      lastName: 'User 2',
      roleName: 'Basic User',
      roleCode: 'sector_user_basic_user',
      contactType: 'Consultant',
      status: 'ACTIVE',
      userId: 'c83e6aa3-0641-4e44-951f-2dacfd28e864',
    },
    {
      firstName: 'Sector 3',
      lastName: 'User 3',
      roleName: 'Administrator User',
      roleCode: 'sector_user_administrator',
      contactType: 'Sector Association',
      status: 'ACTIVE',
      userId: 'ce31db49-4880-40ae-86f6-35eb82dbc48f',
    },
    {
      firstName: 'Sector',
      lastName: 'User 5',
      roleName: 'Administrator User',
      roleCode: 'sector_user_administrator',
      contactType: 'Sector Association',
      status: 'ACTIVE',
      userId: 'a2174d5f-e573-4ace-84a5-f6bf8b5e3dd4',
    },
    {
      firstName: 'Sector',
      lastName: 'User 6',
      roleName: 'Administrator User',
      roleCode: 'sector_user_administrator',
      contactType: 'Sector Association',
      status: 'DISABLED',
      userId: '123asd-123ascddffgh-123xcds45-f6bf8b5e3dd4',
    },
  ],
  editable: true,
} as const;

export const duplicateEmailResponse = {
  code: 'AUTHORITY1005',
  message: 'User status cannot be updated',
  security: true,
  data: [[]],
};

export const userAlreadyExists = {
  code: 'USER1001',
  message: 'User is already registered',
  security: true,
  data: [[]],
};

export const mockPhoneCodeCoutries = {
  COUNTRIES: [
    {
      code: 'AD',
      name: 'Andorra',
      officialName: 'The Principality of Andorra',
    },
    {
      code: 'AE',
      name: 'United Arab Emirates',
      officialName: 'The United Arab Emirates',
    },
    {
      code: 'AF',
      name: 'Afghanistan',
      officialName: 'The Islamic Republic of Afghanistan',
    },
    {
      code: 'AG',
      name: 'Antigua and Barbuda',
      officialName: 'Antigua and Barbuda',
    },
    {
      code: 'AL',
      name: 'Albania',
      officialName: 'The Republic of Albania',
    },
  ],
};
export const mockTargetUnits: TargetUnitAccountInfoResponseDTO = {
  accountsWithSiteContact: [
    {
      accountId: 1,
      businessId: 'ACCT_1',
      accountName: 'Account_1',
      status: 'NEW',
      siteContactUserId: 'c83e6aa3-0641-4e44-951f-2dacfd28e864',
    },
    {
      accountId: 2,
      businessId: 'ACCT_2',
      accountName: 'Account_2',
      status: 'NEW',
    },
    {
      accountId: 3,
      businessId: 'ACCT_3',
      accountName: 'Account_3',
      status: 'NEW',
    },
  ],
  editable: true,
  totalItems: 3,
};
export const mockTargetUnitsNotEditable: TargetUnitAccountInfoResponseDTO = {
  accountsWithSiteContact: [
    {
      accountId: 1,
      businessId: 'ACCT_1',
      accountName: 'Account_1',
      status: 'NEW',
      siteContactUserId: 'c83e6aa3-0641-4e44-951f-2dacfd28e864',
    },
    {
      accountId: 2,
      businessId: 'ACCT_2',
      accountName: 'Account_2',
      status: 'NEW',
    },
    {
      accountId: 3,
      businessId: 'ACCT_3',
      accountName: 'Account_3',
      status: 'NEW',
    },
  ],
  editable: false,
  totalItems: 3,
};

export const mockTargetUnitAccountDetails: TargetUnitAccountDetailsDTO = {
  id: 1,
  businessId: 'AIC-T00001',
  status: 'LIVE',
  name: 'Target unit name 01',
  operatorType: 'LIMITED_COMPANY',
  companyRegistrationNumber: '2636942',
  sicCodes: ['01110'],
  sectorAssociationId: 1,
  subsectorAssociationId: 1,
  financialIndependenceStatus: 'FINANCIALLY_INDEPENDENT',
  address: {
    line1: 'Line 1',
    line2: 'Line 2',
    city: 'City',
    postcode: 'SE23 6FH',
    county: 'County',
    country: 'GR',
  },
  responsiblePerson: {
    firstName: 'John',
    lastName: 'Doe',
    jobTitle: 'Job title 1',
    email: 'responsible@test.gr',
    address: {
      line1: 'Line 1',
      line2: 'Line 2',
      city: 'City 2',
      postcode: 'SE23 6FH2',
      county: 'County2',
      country: 'GR',
    },
    phoneNumber: {
      countryCode: '30',
      number: '6999999999',
    },
  },
  administrativeContactDetails: {
    firstName: 'John',
    lastName: 'Doe',
    jobTitle: 'Job title 2',
    email: 'administrative@test.gr',
    address: {
      line1: 'Line 2',
      line2: 'Line 22',
      city: 'City 2',
      postcode: 'SE23 6FH2',
      county: 'County2',
      country: 'GR',
    },
    phoneNumber: {
      countryCode: '30',
      number: '6999999999',
    },
  },
};

export const mockUnderlyingAgreementDetails: UnderlyingAgreementDetailsDTO = {
  id: 1,
  underlyingAgreementDocumentMap: {
    ['CCA_2']: {
      activationDate: '2024-09-30',
      fileDocument: {
        name: 'ADS_1-T00002 CCA2 Underlying Agreement v1.pdf',
        uuid: '3a4ac334-73d0-492d-9639-a7e2f4bdd647',
      },
    },
    ['CCA_3']: {
      activationDate: '2025-09-30',
      fileDocument: {
        name: 'ADS_1-T00002 CCA3 Underlying Agreement v1.pdf',
        uuid: '3a4ac334-73d0-492d-9639-a7e2f4bdd648',
      },
    },
  },
};

export const mockTargetUnitAccount = (sectorAssociationId) => ({
  targetUnitAccountDetails: {
    id: 4,
    businessId: 'ADS_1-T00004',
    status: 'NEW',
    name: 'operator name',
    operatorType: 'SOLE_TRADER',
    companyRegistrationNumber: '2636942',
    sicCodes: ['sicCode'],
    sectorAssociationId,
    financialIndependenceStatus: 'NON_FINANCIALLY_INDEPENDENT',
    address: {
      line1: 'lin1',
      line2: 'line2',
      city: 'city',
      postcode: '14',
      country: 'GR',
    },
    responsiblePerson: {
      email: 'email@abc.com',
      firstName: 'firstName',
      lastName: 'lastName',
      jobTitle: 'jobTitle',
      contactType: 'RESPONSIBLE_PERSON',
      address: {
        line1: 'lin1',
        line2: 'line2',
        city: 'city',
        postcode: '14',
        country: 'GR',
      },
      phoneNumber: {
        countryCode: '31',
        number: '2323234',
      },
    },
    administrativeContactDetails: {
      email: 'email@abc.com',
      firstName: 'firstName',
      lastName: 'lastName',
      jobTitle: 'jobTitle',
      contactType: 'ADMINISTRATIVE_CONTACT_DETAILS',
      address: {
        line1: 'lin1',
        line2: 'line2',
        city: 'city',
        postcode: '14',
        country: 'GR',
      },
      phoneNumber: {},
    },
  },
});

export const mockAuthState: AuthState = {
  user: { email: 'reg@cca.uk', firstName: 'regulator', lastName: 'england' },
  userProfile: undefined,
  userState: { roleType: 'REGULATOR', status: 'ENABLED' },
  userTerms: undefined,
  isLoggedIn: true,
};

export const mockOperatorAuthorities: OperatorAuthoritiesInfoDTO = {
  authorities: [
    {
      firstName: 'Operator',
      lastName: 'User 2',
      roleName: 'Operator',
      roleCode: 'operator_basic_user',
      contactType: 'Operator',
      status: 'ACTIVE',
      authorityCreationDate: '2024-06-18T13:24:56.936481Z',
      userId: 'e7de58d5-0256-42a7-9501-014d25d5d310',
    },
    {
      firstName: 'Operator',
      lastName: 'User 2',
      roleName: 'Operator',
      roleCode: 'operator_basic_user',
      contactType: 'Operator',
      status: 'PENDING',
      authorityCreationDate: '2024-06-18T13:26:35.001374Z',
      userId: '74b8d6f2-35b3-4f39-8836-abbcfbfadce2',
    },
    {
      firstName: 'Operator',
      lastName: 'User 3',
      roleName: 'Operator',
      roleCode: 'operator_basic_user',
      contactType: 'Consultant',
      status: 'ACTIVE',
      authorityCreationDate: '2024-06-18T13:27:24.137414Z',
      userId: '9375fb9d-7f0f-4ccd-a8b7-fcb1fdd7af9a',
    },
  ],
  editable: true,
};

export const mockOperatorAuthoritiesNotEditable: OperatorAuthoritiesInfoDTO = {
  authorities: [
    {
      firstName: 'Operator',
      lastName: 'User 2',
      roleName: 'Operator',
      roleCode: 'operator_basic_user',
      contactType: 'Operator',
      status: 'ACTIVE',
      authorityCreationDate: '2024-06-18T13:24:56.936481Z',
      userId: 'e7de58d5-0256-42a7-9501-014d25d5d310',
    },
    {
      firstName: 'Operator',
      lastName: 'User 2',
      roleName: 'Operator',
      roleCode: 'operator_basic_user',
      contactType: 'Operator',
      status: 'PENDING',
      authorityCreationDate: '2024-06-18T13:26:35.001374Z',
      userId: '74b8d6f2-35b3-4f39-8836-abbcfbfadce2',
    },
    {
      firstName: 'Operator',
      lastName: 'User 3',
      roleName: 'Operator',
      roleCode: 'operator_basic_user',
      contactType: 'Consultant',
      status: 'ACTIVE',
      authorityCreationDate: '2024-06-18T13:27:24.137414Z',
      userId: '9375fb9d-7f0f-4ccd-a8b7-fcb1fdd7af9a',
    },
  ],
  editable: false,
};

export const mockTargetUnitOperatorDetails: CcaOperatorUserDetailsDTO = {
  email: 'op1tu@cca.uk',
  firstName: 'oper1',
  lastName: 'tu',
  status: 'PENDING',
  phoneNumber: {
    countryCode: '44',
    number: '1234567890',
  },
  mobileNumber: {
    countryCode: '44',
    number: '1234567890',
  },
  jobTitle: 'job12',
  contactType: 'CONSULTANT',
  organisationName: 'organisation',
};
