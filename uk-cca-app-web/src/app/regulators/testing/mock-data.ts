import {
  AuthorityManagePermissionDTO,
  RegulatorUserDTO,
  RegulatorUsersAuthoritiesInfoDTO,
  UserStateDTO,
} from 'cca-api';

export const mockRegulatorsRouteData: { regulators: RegulatorUsersAuthoritiesInfoDTO } = {
  regulators: {
    caUsers: [
      {
        userId: '1reg',
        firstName: 'Alfyn',
        lastName: 'Octo',
        authorityStatus: 'DISABLED',
        locked: false,
        authorityCreationDate: '2020-12-14T12:38:12.846716Z',
      },
      {
        userId: '2reg',
        firstName: 'Therion',
        lastName: 'Path',
        authorityStatus: 'ACTIVE',
        locked: true,
        authorityCreationDate: '2020-12-15T12:38:12.846716Z',
      },
      {
        userId: '3reg',
        firstName: 'Olberik',
        lastName: 'Traveler',
        authorityStatus: 'ACTIVE',
        locked: true,
        authorityCreationDate: '2020-11-10T12:38:12.846716Z',
      },
      {
        userId: '4reg',
        firstName: 'andrew',
        lastName: 'webber',
        authorityStatus: 'ACTIVE',
        locked: false,
        authorityCreationDate: '2021-01-10T12:38:12.846716Z',
      },
      {
        userId: '5reg',
        firstName: 'William',
        lastName: 'Walker',
        authorityStatus: 'PENDING',
        locked: true,
        authorityCreationDate: '2021-02-8T12:38:12.846716Z',
      },
    ],
    editable: true,
  },
};

export const mockRegulatorUserStatus: UserStateDTO = {
  status: 'ENABLED',
  roleType: 'REGULATOR',
  userId: '111',
};

export const mockRegulatorUser: {
  user: RegulatorUserDTO;
  permissions: AuthorityManagePermissionDTO;
} = {
  user: {
    email: 'test@host.com',
    firstName: 'John',
    lastName: 'Doe',
    jobTitle: 'developer',
    phoneNumber: '23456',
    mobileNumber: '55444',
    signature: {
      name: 'mySignature.bmp',
      uuid: '60fe9548-ac65-492a-b057-60033b0fbbed',
    },
  },
  permissions: {
    editable: true,
    permissions: {
      ASSIGN_REASSIGN_TASKS: 'NONE',
      MANAGE_USERS_AND_CONTACTS: 'NONE',
      // REVIEW_ORGANISATION_ACCOUNT: 'VIEW_ONLY',
    },
  },
};

export const mockRegulatorRolePermissions = [
  // 'REVIEW_ORGANISATION_ACCOUNT',
  'MANAGE_USERS_AND_CONTACTS',
  'ASSIGN_REASSIGN_TASKS',
];

export const mockRegulatorBasePermissions = [
  {
    name: 'Regulator Administrator',
    code: 'regulator_administrator',
    rolePermissions: {
      // REVIEW_ORGANISATION_ACCOUNT: 'VIEW_ONLY',
      MANAGE_USERS_AND_CONTACTS: 'NONE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
    },
  },
  {
    name: 'Regulator technical consultant',
    code: 'regulator_technical_consultant',
    rolePermissions: {
      // REVIEW_ORGANISATION_ACCOUNT: 'EXECUTE',
      MANAGE_USERS_AND_CONTACTS: 'NONE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
    },
  },
  {
    name: 'CA super user',
    code: 'ca_super_user',
    rolePermissions: {
      // REVIEW_ORGANISATION_ACCOUNT: 'EXECUTE',
      MANAGE_USERS_AND_CONTACTS: 'EXECUTE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
    },
  },
  {
    name: 'Service super user',
    code: 'service_super_user',
    rolePermissions: {
      // REVIEW_ORGANISATION_ACCOUNT: 'EXECUTE',
      MANAGE_USERS_AND_CONTACTS: 'EXECUTE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
    },
  },
  {
    name: 'Regulator technical officer',
    code: 'regulator_technical_officer',
    rolePermissions: {
      // REVIEW_ORGANISATION_ACCOUNT: 'EXECUTE',
      MANAGE_USERS_AND_CONTACTS: 'NONE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
    },
  },
];

export const mockRegulatorPermissionGroups = {
  // REVIEW_ORGANISATION_ACCOUNT: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  MANAGE_USERS_AND_CONTACTS: ['NONE', 'EXECUTE'],
  ASSIGN_REASSIGN_TASKS: ['NONE', 'EXECUTE'],
};
