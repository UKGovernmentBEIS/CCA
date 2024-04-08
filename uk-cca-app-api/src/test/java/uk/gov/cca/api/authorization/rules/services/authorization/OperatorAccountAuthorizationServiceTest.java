package uk.gov.cca.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.services.authorization.OperatorAccountAuthorizationService;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorAccountAuthorizationServiceTest {
    private final OperatorAccountAuthorizationService operatorAccountAuthorizationService = new OperatorAccountAuthorizationService();
    private final AppAuthority appAuthority = AppAuthority.builder()
            .accountId(1L)
            .permissions(List.of(Permission.PERM_ACCOUNT_USERS_EDIT,
                    Permission.PERM_TASK_ASSIGNMENT))
            .build();
    private final AppUser user = AppUser.builder().authorities(List.of(appAuthority)).roleType(RoleType.OPERATOR).build();


    @Test
    void isAuthorized_account_true() {
        assertTrue(operatorAccountAuthorizationService.isAuthorized(user, 1L));
    }

    @Test
    void isAuthorized_account_false() {
        assertFalse(operatorAccountAuthorizationService.isAuthorized(user, 2L));
    }

    @Test
    void isAuthorized_account_with_permissions_true() {
        assertTrue(operatorAccountAuthorizationService.isAuthorized(user, 1L, Permission.PERM_ACCOUNT_USERS_EDIT));
    }

    @Test
    void isAuthorized_account_with_permissions_false() {
        assertFalse(operatorAccountAuthorizationService.isAuthorized(user, 1L, Permission.PERM_CA_USERS_EDIT));
    }

    @Test
    void getType() {
        assertEquals(RoleType.OPERATOR, operatorAccountAuthorizationService.getRoleType());
    }
}