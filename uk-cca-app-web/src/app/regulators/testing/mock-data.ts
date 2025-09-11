import { RegulatorUsersAuthoritiesInfoDTO, SectorUsersAuthoritiesInfoDTO, UserStateDTO } from 'cca-api';

export const mockRegulatorPermissionGroups = {
  MANAGE_SECTOR_ASSOCIATIONS: ['NONE', 'EXECUTE'],
  MANAGE_USERS_AND_CONTACTS: ['NONE', 'EXECUTE'],
  ASSIGN_REASSIGN_TASKS: ['NONE', 'EXECUTE'],
  MANAGE_SECTOR_USERS: ['NONE', 'EXECUTE'],
  ADMIN_TERMINATION_SUBMISSION: ['NONE', 'EXECUTE'],
  ADMIN_TERMINATION_PEER_REVIEW: ['NONE', 'EXECUTE'],
};

export const mockRegulatorsRouteData: { regulators: RegulatorUsersAuthoritiesInfoDTO } = {
  regulators: {
    caUsers: [
      {
        userId: '1reg',
        firstName: 'Alfyn',
        lastName: 'Octo',
        authorityStatus: 'DISABLED',
        authorityCreationDate: '2020-12-14T12:38:12.846716Z',
      },
      {
        userId: '2reg',
        firstName: 'Therion',
        lastName: 'Path',
        authorityStatus: 'ACTIVE',
        authorityCreationDate: '2020-12-15T12:38:12.846716Z',
      },
      {
        userId: '3reg',
        firstName: 'Olberik',
        lastName: 'Traveler',
        authorityStatus: 'ACTIVE',
        authorityCreationDate: '2020-11-10T12:38:12.846716Z',
      },
      {
        userId: '4reg',
        firstName: 'andrew',
        lastName: 'webber',
        authorityStatus: 'ACTIVE',
        authorityCreationDate: '2021-01-10T12:38:12.846716Z',
      },
      {
        userId: '5reg',
        firstName: 'William',
        lastName: 'Walker',
        authorityStatus: 'PENDING',
        authorityCreationDate: '2021-02-8T12:38:12.846716Z',
      },
    ],
    editable: true,
  },
};

export const mockRegulatorUserState: UserStateDTO = {
  status: 'ENABLED',
  roleType: 'REGULATOR',
  userId: '111',
};

export const mockDetailsRouteDataEdit = {
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
    userPermissions: {
      editable: true,
      permissions: {
        MANAGE_SECTOR_ASSOCIATIONS: 'EXECUTE',
        ASSIGN_REASSIGN_TASKS: 'EXECUTE',
        MANAGE_USERS_AND_CONTACTS: 'EXECUTE',
        MANAGE_SECTOR_USERS: 'EXECUTE',
        ADMIN_TERMINATION_SUBMISSION: 'EXECUTE',
        ADMIN_TERMINATION_PEER_REVIEW: 'EXECUTE',
      },
    },
    permissionGroupLevels: mockRegulatorPermissionGroups,
  },
};
export const mockDetailsRouteDataView = {
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
    userPermissions: {
      editable: false,
      permissions: {
        MANAGE_SECTOR_ASSOCIATIONS: 'NONE',
        ASSIGN_REASSIGN_TASKS: 'NONE',
        MANAGE_USERS_AND_CONTACTS: 'NONE',
        MANAGE_SECTOR_USERS: 'NONE',
        ADMIN_TERMINATION_SUBMISSION: 'NONE',
        ADMIN_TERMINATION_PEER_REVIEW: 'NONE',
      },
    },
  },
};
export const mockDetailsRouteDataAdd = {
  permissions: {
    userPermissions: {
      editable: true,
    },
    permissionGroupLevels: mockRegulatorPermissionGroups,
  },
};

export const mockRegulatorRolePermissions = [
  'MANAGE_SECTOR_ASSOCIATIONS',
  'MANAGE_USERS_AND_CONTACTS',
  'ASSIGN_REASSIGN_TASKS',
  'MANAGE_SECTOR_USERS',
  'ADMIN_TERMINATION_SUBMISSION',
  'ADMIN_TERMINATION_PEER_REVIEW',
  'UNDERLYING_AGREEMENT_VARIATION_REVIEW',
  'UNDERLYING_AGREEMENT_APPLICATION_REVIEW',
];

export const mockRegulatorBasePermissions = [
  {
    name: 'Administrator User',
    code: 'regulator_administrator',
    rolePermissions: {
      MANAGE_SECTOR_ASSOCIATIONS: 'EXECUTE',
      MANAGE_USERS_AND_CONTACTS: 'EXECUTE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
      MANAGE_SECTOR_USERS: 'EXECUTE',
      ADMIN_TERMINATION_SUBMISSION: 'EXECUTE',
      ADMIN_TERMINATION_PEER_REVIEW: 'EXECUTE',
      UNDERLYING_AGREEMENT_VARIATION_REVIEW: 'EXECUTE',
      UNDERLYING_AGREEMENT_APPLICATION_REVIEW: 'EXECUTE',
    },
  },
  {
    name: 'Regulator basic user',
    code: 'regulator_basic_user',
    rolePermissions: {
      MANAGE_SECTOR_ASSOCIATIONS: 'NONE',
      MANAGE_USERS_AND_CONTACTS: 'NONE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
      MANAGE_SECTOR_USERS: 'NONE',
      ADMIN_TERMINATION_SUBMISSION: 'NONE',
      ADMIN_TERMINATION_PEER_REVIEW: 'NONE',
      UNDERLYING_AGREEMENT_VARIATION_REVIEW: 'NONE',
      UNDERLYING_AGREEMENT_APPLICATION_REVIEW: 'NONE',
    },
  },
];

export const mockUserAuthorities: SectorUsersAuthoritiesInfoDTO = {
  editable: true,
  authorities: [
    {
      firstName: 'fn 1',
      lastName: 'ln 1',
      roleCode: 'sector_user_administrator',
      roleName: 'Administrator User',
      contactType: 'contact type 1',
      status: 'ACTIVE',
      userId: '1',
    },
    {
      firstName: 'fn 2',
      lastName: 'ln 2',
      roleCode: 'sector_user_basic_user',
      roleName: 'Basic User',
      contactType: 'contact type 2',
      status: 'ACTIVE',
      userId: '2',
    },
  ],
};
