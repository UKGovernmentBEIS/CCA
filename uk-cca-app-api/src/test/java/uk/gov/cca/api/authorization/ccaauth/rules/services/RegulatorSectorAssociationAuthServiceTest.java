package uk.gov.cca.api.authorization.ccaauth.rules.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SectorAssociationAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Permission;
import uk.gov.netz.api.authorization.rules.services.authorization.RegulatorCompAuthAuthorizationService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class RegulatorSectorAssociationAuthServiceTest {

    @InjectMocks
    private RegulatorSectorAssociationAuthService regulatorSectorAssociationAuthService;

    @Mock
    private RegulatorCompAuthAuthorizationService regulatorCompAuthAuthorizationService;

    @Mock
    private SectorAssociationAuthorityInfoProvider sectorAssociationAuthorityInfoProvider;

    @Test
    void isAuthorized_true() {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().roleType(REGULATOR).build();
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;

        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorAssociationId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(true);

        assertTrue(regulatorSectorAssociationAuthService.isAuthorized(user, sectorAssociationId));
    }

    @Test
    void isAuthorized_false() {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().roleType(REGULATOR).build();
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;

        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorAssociationId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(false);

        assertFalse(regulatorSectorAssociationAuthService.isAuthorized(user, sectorAssociationId));
    }

    @Test
    void isAuthorized_with_permissions_true() {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().roleType(REGULATOR).build();
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String permission = Permission.PERM_TASK_ASSIGNMENT;

        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorAssociationId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, permission)).thenReturn(true);

        assertTrue(regulatorSectorAssociationAuthService.isAuthorized(user, sectorAssociationId, permission));
    }

    @Test
    void isAuthorized_with_permissions_false() {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().roleType(REGULATOR).build();
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String permission = Permission.PERM_TASK_ASSIGNMENT;

        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorAssociationId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, permission)).thenReturn(false);

        assertFalse(regulatorSectorAssociationAuthService.isAuthorized(user, sectorAssociationId, permission));
    }

    @Test
    void getRoleType() {
        assertEquals(REGULATOR, regulatorSectorAssociationAuthService.getRoleType());
    }
}
