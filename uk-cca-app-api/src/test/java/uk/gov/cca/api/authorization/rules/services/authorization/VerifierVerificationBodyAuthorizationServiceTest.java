package uk.gov.cca.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.services.authorization.VerifierVerificationBodyAuthorizationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.netz.api.common.domain.RoleType.VERIFIER;

class VerifierVerificationBodyAuthorizationServiceTest {

    private VerifierVerificationBodyAuthorizationService authorizationService = new VerifierVerificationBodyAuthorizationService();
    private final AppAuthority appAuthority = AppAuthority.builder()
        .verificationBodyId(1L)
        .permissions(List.of(Permission.PERM_TASK_ASSIGNMENT, Permission.PERM_CA_USERS_EDIT))
        .build();
    private final AppUser verifierUser = AppUser.builder().authorities(List.of(appAuthority)).roleType(VERIFIER).build();

    @Test
    void isAuthorized_account_true() {
        assertTrue(authorizationService.isAuthorized(verifierUser, 1L));
    }

    @Test
    void isAuthorized_account_false() {
        assertFalse(authorizationService.isAuthorized(verifierUser, 2L));
    }

    @Test
    void isAuthorized_account_with_permissions_true() {
        assertTrue(authorizationService.isAuthorized(verifierUser, 1L, Permission.PERM_CA_USERS_EDIT));
    }

    @Test
    void isAuthorized_account_with_permissions_false() {
        assertFalse(authorizationService.isAuthorized(verifierUser, 1L, Permission.PERM_ACCOUNT_USERS_EDIT));
    }

    @Test
    void getType() {
        assertEquals(VERIFIER, authorizationService.getRoleType());
    }
}