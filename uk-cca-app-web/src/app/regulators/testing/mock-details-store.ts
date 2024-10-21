import { DetailsStoreType } from '../details/details.store';

export const editorUserState: DetailsStoreType = {
  isAdd: false,
  isEditable: true,
  userPermissions: {
    MANAGE_USERS_AND_CONTACTS: 'EXECUTE',
    ASSIGN_REASSIGN_TASKS: 'EXECUTE',
    MANAGE_SECTOR_ASSOCIATIONS: 'NONE',
    MANAGE_SECTOR_USERS: 'NONE',
    ADMIN_TERMINATION_SUBMISSION: 'NONE',
  },
  regulatorRoles: [
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
  ],
  permissionGroupLevels: {
    MANAGE_USERS_AND_CONTACTS: ['NONE', 'EXECUTE'],
    ASSIGN_REASSIGN_TASKS: ['NONE', 'EXECUTE'],
    MANAGE_SECTOR_ASSOCIATIONS: ['NONE', 'EXECUTE'],
    MANAGE_SECTOR_USERS: ['NONE', 'EXECUTE'],
    ADMIN_TERMINATION_SUBMISSION: ['NONE', 'EXECUTE'],
  },
  user: {
    email: 'regulator_admin@cca.uk',
    firstName: 'Regulator',
    lastName: 'Admin',
    jobTitle: 'asd',
    phoneNumber: '123123',
    mobileNumber: '12123123123',
    signature: {
      name: 'SampleSignature.bmp',
      uuid: 'b3f46e89-7f65-4bc6-8c3d-84ea448e32ec',
    },
  },
};
export const addUserState: DetailsStoreType = {
  isAdd: true,
  isEditable: true,
  permissionGroupLevels: {
    MANAGE_USERS_AND_CONTACTS: ['NONE', 'EXECUTE'],
    ASSIGN_REASSIGN_TASKS: ['NONE', 'EXECUTE'],
    MANAGE_SECTOR_ASSOCIATIONS: ['NONE', 'EXECUTE'],
    MANAGE_SECTOR_USERS: ['NONE', 'EXECUTE'],
    ADMIN_TERMINATION_SUBMISSION: ['NONE', 'EXECUTE'],
  },
  regulatorRoles: [
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
  ],
};
export const viewerUserState: DetailsStoreType = {
  isAdd: false,
  isEditable: false,
  userPermissions: {
    MANAGE_USERS_AND_CONTACTS: 'NONE',
    ASSIGN_REASSIGN_TASKS: 'EXECUTE',
    MANAGE_SECTOR_ASSOCIATIONS: 'NONE',
    MANAGE_SECTOR_USERS: 'NONE',
    ADMIN_TERMINATION_SUBMISSION: 'NONE',
  },
  user: {
    email: 'regulator_user_2@cca.uk',
    firstName: 'Regulator3',
    lastName: 'User3',
    status: 'REGISTERED',
    jobTitle: 'some job title 3',
    phoneNumber: '123',
    mobileNumber: '12123123123365',
    signature: {
      name: 'SampleSignature.bmp',
      uuid: '9e53c099-9165-4967-beb5-0076e1fc5505',
    },
  },
};
