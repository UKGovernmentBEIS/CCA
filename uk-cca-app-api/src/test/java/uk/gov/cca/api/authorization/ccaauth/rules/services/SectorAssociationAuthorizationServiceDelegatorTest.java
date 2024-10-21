package uk.gov.cca.api.authorization.ccaauth.rules.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Permission;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class SectorAssociationAuthorizationServiceDelegatorTest {

    private SectorAssociationAuthorizationServiceDelegator serviceDelegator;
    private RegulatorSectorAssociationAuthService regulatorSectorAssociationAuthService;
    private SectorUserSectorAssociationAuthService sectorUserSectorAssociationAuthService;

    @BeforeAll
    void setup() {
        regulatorSectorAssociationAuthService = Mockito.mock(RegulatorSectorAssociationAuthService.class);
        sectorUserSectorAssociationAuthService = Mockito.mock(SectorUserSectorAssociationAuthService.class);
        serviceDelegator = new SectorAssociationAuthorizationServiceDelegator(
            List.of(regulatorSectorAssociationAuthService, sectorUserSectorAssociationAuthService));
        when(regulatorSectorAssociationAuthService.getRoleType()).thenReturn(REGULATOR);
        when(sectorUserSectorAssociationAuthService.getRoleType()).thenReturn(SECTOR_USER);
    }

    @Test
    void isAuthorized_sector_user_no_permissions() {
        Long sectorAssociationId = 1L;
        AppUser sectorUser = AppUser.builder().roleType(SECTOR_USER).build();
        when(sectorUserSectorAssociationAuthService.isAuthorized(sectorUser, sectorAssociationId)).thenReturn(true);

        assertTrue(serviceDelegator.isAuthorized(sectorUser, sectorAssociationId));

        verify(sectorUserSectorAssociationAuthService, times(1))
            .isAuthorized(sectorUser, sectorAssociationId);
    }

    @Test
    void isAuthorized_sector_user_with_permissions() {
        Long sectorAssociationId = 1L;
        AppUser sectorUser = AppUser.builder().roleType(SECTOR_USER).build();
        String permission = Permission.PERM_ACCOUNT_USERS_EDIT;
        when(sectorUserSectorAssociationAuthService.isAuthorized(sectorUser, sectorAssociationId, permission)).thenReturn(true);

        assertTrue(serviceDelegator.isAuthorized(sectorUser, sectorAssociationId, permission));

        verify(sectorUserSectorAssociationAuthService, times(1))
            .isAuthorized(sectorUser, sectorAssociationId, permission);
    }

    @Test
    void isAuthorized_regulator_no_permissions() {
        Long sectorAssociationId = 1L;
        AppUser regulatorUser = AppUser.builder().roleType(REGULATOR).build();
        when(regulatorSectorAssociationAuthService.isAuthorized(regulatorUser, sectorAssociationId)).thenReturn(true);

        assertTrue(serviceDelegator.isAuthorized(regulatorUser, sectorAssociationId));

        verify(regulatorSectorAssociationAuthService, times(1))
            .isAuthorized(regulatorUser, sectorAssociationId);
    }

    @Test
    void isAuthorized_regulator_with_permissions() {
        Long sectorAssociationId = 1L;
        AppUser regulatorUser = AppUser.builder().roleType(REGULATOR).build();
        String permission = Permission.PERM_ACCOUNT_USERS_EDIT;
        when(regulatorSectorAssociationAuthService.isAuthorized(regulatorUser, sectorAssociationId, permission)).thenReturn(true);

        assertTrue(serviceDelegator.isAuthorized(regulatorUser, sectorAssociationId, permission));

        verify(regulatorSectorAssociationAuthService, times(1))
            .isAuthorized(regulatorUser, sectorAssociationId, permission);
    }
}