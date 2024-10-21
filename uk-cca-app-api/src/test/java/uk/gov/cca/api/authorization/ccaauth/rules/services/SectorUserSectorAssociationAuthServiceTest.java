package uk.gov.cca.api.authorization.ccaauth.rules.services;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Permission;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

class SectorUserSectorAssociationAuthServiceTest {

    private final SectorUserSectorAssociationAuthService service = new SectorUserSectorAssociationAuthService();

    @Test
    void isAuthorized_true() {
        Long sectorAssociationId = 1L;
        AppCcaAuthority appAuthority = AppCcaAuthority.builder()
            .sectorAssociationId(sectorAssociationId)
            .permissions(List.of(Permission.PERM_TASK_ASSIGNMENT, Permission.PERM_ACCOUNT_USERS_EDIT))
            .build();
        AppUser user = AppUser.builder()
            .roleType(SECTOR_USER)
            .authorities(List.of(appAuthority))
            .build();

        assertTrue(service.isAuthorized(user, sectorAssociationId));
    }

    @Test
    void isAuthorized_false() {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder()
            .roleType(SECTOR_USER)
            .build();

        assertFalse(service.isAuthorized(user, sectorAssociationId));
    }

    @Test
    void isAuthorized_with_permissions_true() {
        Long sectorAssociationId = 1L;
        AppCcaAuthority appAuthority = AppCcaAuthority.builder()
            .sectorAssociationId(sectorAssociationId)
            .permissions(List.of(Permission.PERM_TASK_ASSIGNMENT, Permission.PERM_ACCOUNT_USERS_EDIT))
            .build();
        AppUser user = AppUser.builder()
            .roleType(SECTOR_USER)
            .authorities(List.of(appAuthority))
            .build();

        assertTrue(service.isAuthorized(user, sectorAssociationId, Permission.PERM_ACCOUNT_USERS_EDIT));
    }

    @Test
    void isAuthorized_with_permissions_false() {
        Long sectorAssociationId = 1L;
        AppCcaAuthority appAuthority = AppCcaAuthority.builder()
            .sectorAssociationId(sectorAssociationId)
            .permissions(List.of(Permission.PERM_TASK_ASSIGNMENT, Permission.PERM_ACCOUNT_USERS_EDIT))
            .build();
        AppUser user = AppUser.builder()
            .roleType(SECTOR_USER)
            .authorities(List.of(appAuthority))
            .build();

        assertFalse(service.isAuthorized(user, sectorAssociationId, Permission.PERM_CA_USERS_EDIT));
    }

    @Test
    void getRoleType() {
        assertEquals(SECTOR_USER, service.getRoleType());
    }
}