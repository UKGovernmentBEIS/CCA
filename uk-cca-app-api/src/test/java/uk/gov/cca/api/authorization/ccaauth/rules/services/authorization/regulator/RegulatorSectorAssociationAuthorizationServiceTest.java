package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.regulator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SectorAssociationAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.regulator.RegulatorCompetentAuthorityAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.mockito.Mockito.when;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class RegulatorSectorAssociationAuthorizationServiceTest {
    @InjectMocks
    private RegulatorSectorAssociationAuthorizationService regulatorSectorAssociationAuthorizationService;

    @Mock
    private SectorAssociationAuthorityInfoProvider sectorAssociationAuthorityInfoProvider;

    @Mock
    private RegulatorCompetentAuthorityAuthorizationService regulatorCompAuthAuthorizationService;

    private final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

    @Test
    void isAuthorized_account_with_criteria_true() {
        Long sectorId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()))
                .build();
        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(true);

        Assertions.assertTrue(regulatorSectorAssociationAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_with_criteria_with_permission_true() {
        Long sectorId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()))
                .permission("permission1")
                .build();
        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, "permission1")).thenReturn(true);

        Assertions.assertTrue(regulatorSectorAssociationAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_with_criteria_false() {
        Long sectorId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()))
                .build();
        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorId)).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(false);

        Assertions.assertFalse(regulatorSectorAssociationAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_with_criteria_with_permission_false() {
        Long sectorId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()))
                .permission("permission1")
                .build();
        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorId)).thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, "permission1")).thenReturn(false);

        Assertions.assertFalse(regulatorSectorAssociationAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_true() {
        Long sectorId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(true);

        Assertions.assertTrue(regulatorSectorAssociationAuthorizationService.isAuthorized(user, sectorId));
    }

    @Test
    void isAuthorized_account_false() {
        Long sectorId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(false);

        Assertions.assertFalse(regulatorSectorAssociationAuthorizationService.isAuthorized(user, sectorId));
    }

    @Test
    void isAuthorized_account_with_permissions_true() {
        Long sectorId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String permission = "permission1";
        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, permission)).thenReturn(true);

        Assertions.assertTrue(regulatorSectorAssociationAuthorizationService.isAuthorized(user, sectorId, permission));
    }

    @Test
    void isAuthorized_account_with_permissions_false() {
        Long sectorId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String permission = "permission1";
        when(sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, permission)).thenReturn(false);

        Assertions.assertFalse(regulatorSectorAssociationAuthorizationService.isAuthorized(user, sectorId, permission));
    }

    @Test
    void isApplicable_true() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "1"))
                .build();
        Assertions.assertTrue(regulatorSectorAssociationAuthorizationService.isApplicable(authorizationCriteria));
    }

    @Test
    void isApplicable_false() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .build();
        Assertions.assertFalse(regulatorSectorAssociationAuthorizationService.isApplicable(authorizationCriteria));
    }
}