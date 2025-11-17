package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.sectoruser;

import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.FacilityAuthorityInfoProvider;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.TargetUnitAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.constants.RoleTypeConstants;

@ExtendWith(MockitoExtension.class)
class SectorUserFacilityAuthorizationServiceTest {

	@InjectMocks
    private SectorUserFacilityAuthorizationService sectorUserFacilityAuthorizationService;

    @Mock
    private FacilityAuthorityInfoProvider facilityAuthorityInfoProvider;
    
    @Mock
    private TargetUnitAuthorityInfoProvider targetUnitAuthorityInfoProvider;

    @Mock
    private SectorUserSectorAssociationAuthorizationService sectorUserSectorAssociationAuthorizationService;

    private final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

    @Test
    void isAuthorized_with_criteria_true() {
        Long facilityId = 1L;
        Long accountId = 2L;
        Long sectorId = 3L;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .build();
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId)).thenReturn(true);

        Assertions.assertTrue(sectorUserFacilityAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_with_criteria_with_permission_true() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        Long sectorId = 3L;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .permission("permission1")
                .build();
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId, "permission1")).thenReturn(true);

        Assertions.assertTrue(sectorUserFacilityAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_with_criteria_false() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        Long sectorId = 3L;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .build();
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId)).thenReturn(false);

        Assertions.assertFalse(sectorUserFacilityAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_with_criteria_with_permission_false() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        Long sectorId = 3L;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .permission("permission1")
                .build();
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId, "permission1")).thenReturn(false);

        Assertions.assertFalse(sectorUserFacilityAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_true() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        Long sectorId = 3L;
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId)).thenReturn(true);

        Assertions.assertTrue(sectorUserFacilityAuthorizationService.isAuthorized(user, facilityId));
    }

    @Test
    void isAuthorized_false() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        Long sectorId = 3L;
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId)).thenReturn(false);

        Assertions.assertFalse(sectorUserFacilityAuthorizationService.isAuthorized(user, facilityId));
    }

    @Test
    void isAuthorized_with_permissions_true() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        Long sectorId = 3L;
        String permission = "permission1";
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId, permission)).thenReturn(true);

        Assertions.assertTrue(sectorUserFacilityAuthorizationService.isAuthorized(user, facilityId, permission));
    }

    @Test
    void isAuthorized_with_permissions_false() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        Long sectorId = 3L;
        String permission = "permission1";
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId, permission)).thenReturn(false);

        Assertions.assertFalse(sectorUserFacilityAuthorizationService.isAuthorized(user, facilityId, permission));
    }

    @Test
    void isApplicable_true() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, "1"))
                .build();
        Assertions.assertTrue(sectorUserFacilityAuthorizationService.isApplicable(authorizationCriteria));
    }

    @Test
    void isApplicable_false() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .build();
        Assertions.assertFalse(sectorUserFacilityAuthorizationService.isApplicable(authorizationCriteria));
    }
}
