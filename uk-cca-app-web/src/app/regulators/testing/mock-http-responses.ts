export const executeRegulatorPermissions = {
  permissions: {
    MANAGE_USERS_AND_CONTACTS: 'EXECUTE',
    ASSIGN_REASSIGN_TASKS: 'EXECUTE',
    MANAGE_SECTOR_ASSOCIATIONS: 'NONE',
    MANAGE_SECTOR_USERS: 'NONE',
    ADMIN_TERMINATION_SUBMISSION: 'NONE',
  },
  editable: true,
};

export const readonlyRegulatorPermissions = {
  permissions: {
    MANAGE_USERS_AND_CONTACTS: 'NONE',
    ASSIGN_REASSIGN_TASKS: 'EXECUTE',
    MANAGE_SECTOR_ASSOCIATIONS: 'NONE',
    MANAGE_SECTOR_USERS: 'NONE',
    ADMIN_TERMINATION_SUBMISSION: 'NONE',
  },
  editable: false,
};

export const regulatorRoles = [
  {
    name: 'Administrator User',
    code: 'regulator_administrator',
    rolePermissions: {
      MANAGE_USERS_AND_CONTACTS: 'EXECUTE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
      MANAGE_SECTOR_ASSOCIATIONS: 'EXECUTE',
      MANAGE_SECTOR_USERS: 'EXECUTE',
      ADMIN_TERMINATION_SUBMISSION: 'EXECUTE',
    },
  },
  {
    name: 'Basic User',
    code: 'regulator_basic_user',
    rolePermissions: {
      MANAGE_USERS_AND_CONTACTS: 'NONE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
      MANAGE_SECTOR_ASSOCIATIONS: 'NONE',
      MANAGE_SECTOR_USERS: 'NONE',
      ADMIN_TERMINATION_SUBMISSION: 'NONE',
    },
  },
];

export const permissionGroupLevels = {
  MANAGE_USERS_AND_CONTACTS: ['NONE', 'EXECUTE'],
  ASSIGN_REASSIGN_TASKS: ['NONE', 'EXECUTE'],
  MANAGE_SECTOR_ASSOCIATIONS: ['NONE', 'EXECUTE'],
  MANAGE_SECTOR_USERS: ['NONE', 'EXECUTE'],
  ADMIN_TERMINATION_SUBMISSION: ['NONE', 'EXECUTE'],
};

export const readonlyCurrentUser = {
  email: 'regulator_user_2@cca.uk',
  firstName: 'Regulator3',
  lastName: 'User3',
  status: 'REGISTERED',
  jobTitle: 'some job title 3',
  phoneNumber: '123',
  signature: {
    name: 'SampleSignature.bmp',
    uuid: '9e53c099-9165-4967-beb5-0076e1fc5505',
  },
};
