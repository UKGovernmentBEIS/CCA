package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.sectoruser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SectorUserSectorAssociationAuthorizationServiceTest {

    private final SectorUserSectorAssociationAuthorizationService sectorUserSectorAssociationAuthorizationService = new SectorUserSectorAssociationAuthorizationService();
    private final AppCcaAuthority appAuthority = AppCcaAuthority.builder()
            .sectorAssociationId(1L)
            .permissions(List.of("permission1", "permission2"))
            .build();
    private final AppUser user = AppUser.builder().authorities(List.of(appAuthority)).roleType(CcaRoleTypeConstants.SECTOR_USER).build();

    @Test
    void isAuthorized_account_with_criteria_true() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "1"))
                .build();
        assertTrue(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_with_criteria_with_permission_true() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "1"))
                .permission("permission1")
                .build();
        assertTrue(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_with_criteria_false() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "2"))
                .build();
        assertFalse(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_with_criteria_with_permission_false() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "2"))
                .permission("permission3")
                .build();
        assertFalse(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_true() {
        assertTrue(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, 1L));
    }

    @Test
    void isAuthorized_account_false() {
        assertFalse(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, 2L));
    }

    @Test
    void isAuthorized_account_with_permissions_true() {
        assertTrue(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, 1L, "permission1"));
    }

    @Test
    void isAuthorized_account_with_permissions_false() {
        assertFalse(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, 1L, "permission3"));
    }

    @Test
    void isApplicable_true() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "1"))
                .build();
        assertTrue(sectorUserSectorAssociationAuthorizationService.isApplicable(authorizationCriteria));
    }

    @Test
    void isApplicable_false() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .build();
        assertFalse(sectorUserSectorAssociationAuthorizationService.isApplicable(authorizationCriteria));
    }
}