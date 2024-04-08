package uk.gov.cca.api.authorization.core.domain;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Permission {
    // Permissions for Task Assignment
    public static final String PERM_TASK_ASSIGNMENT = "PERM_TASK_ASSIGNMENT";
    
    // User management
    public static final String PERM_ACCOUNT_USERS_EDIT = "PERM_ACCOUNT_USERS_EDIT";
    public static final String PERM_CA_USERS_EDIT = "PERM_CA_USERS_EDIT";

    public static final String PERM_VB_ACCESS_ALL_ACCOUNTS = "PERM_VB_ACCESS_ALL_ACCOUNTS";
}
