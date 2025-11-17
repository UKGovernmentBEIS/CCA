package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.regulator;

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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.providers.AccountAuthorityInfoProvider;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.regulator.RegulatorCompetentAuthorityAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@ExtendWith(MockitoExtension.class)
class RegulatorFacilityAuthorizationServiceTest {

	@InjectMocks
    private RegulatorFacilityAuthorizationService regulatorFacilityAuthorizationService;

    @Mock
    private FacilityAuthorityInfoProvider facilityAuthorityInfoProvider;
    
    @Mock
    private AccountAuthorityInfoProvider accountAuthorityInfoProvider;

    @Mock
    private RegulatorCompetentAuthorityAuthorizationService regulatorCompAuthAuthorizationService;

    private final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

    @Test
    void isAuthorized_with_criteria_true() {
        Long facilityId = 1L;
        Long accountId = 2L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .build();
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(true);

        Assertions.assertTrue(regulatorFacilityAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_with_criteria_with_permission_true() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .permission("permission1")
                .build();
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, "permission1")).thenReturn(true);

        Assertions.assertTrue(regulatorFacilityAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_with_criteria_false() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .build();
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(false);

        Assertions.assertFalse(regulatorFacilityAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_with_criteria_with_permission_false() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .permission("permission1")
                .build();
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, "permission1")).thenReturn(false);

        Assertions.assertFalse(regulatorFacilityAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_true() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(true);

        Assertions.assertTrue(regulatorFacilityAuthorizationService.isAuthorized(user, facilityId));
    }

    @Test
    void isAuthorized_false() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(false);

        Assertions.assertFalse(regulatorFacilityAuthorizationService.isAuthorized(user, facilityId));
    }

    @Test
    void isAuthorized_with_permissions_true() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String permission = "permission1";
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, permission)).thenReturn(true);

        Assertions.assertTrue(regulatorFacilityAuthorizationService.isAuthorized(user, facilityId, permission));
    }

    @Test
    void isAuthorized_with_permissions_false() {
    	Long facilityId = 1L;
        Long accountId = 2L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String permission = "permission1";
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, permission)).thenReturn(false);

        Assertions.assertFalse(regulatorFacilityAuthorizationService.isAuthorized(user, facilityId, permission));
    }

    @Test
    void isApplicable_true() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, "1"))
                .build();
        Assertions.assertTrue(regulatorFacilityAuthorizationService.isApplicable(authorizationCriteria));
    }

    @Test
    void isApplicable_false() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .build();
        Assertions.assertFalse(regulatorFacilityAuthorizationService.isApplicable(authorizationCriteria));
    }
}
