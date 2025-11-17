package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.operator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.FacilityAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.operator.OperatorAccountAuthorizationService;

@ExtendWith(MockitoExtension.class)
class OperatorFacilityAuthorizationServiceTest {

	@Mock
    private FacilityAuthorityInfoProvider facilityAuthorityInfoProvider;

    @Mock
    private OperatorAccountAuthorizationService operatorAccountAuthorizationService;

    @InjectMocks
    private OperatorFacilityAuthorizationService authService;

    @Test
    void testIsAuthorized_WithValidFacilityId_ShouldReturnTrue() {

        final Long validFacilityId = 1L;
        final AppUser user = AppUser.builder().build();
        final Long accountId = 100L;

        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(validFacilityId))
                .thenReturn(accountId);
        when(operatorAccountAuthorizationService.isAuthorized(user, accountId)).thenReturn(true);
        Map<String, String> requestResources = new HashMap<>(Map.of(CcaResourceType.FACILITY, "1"));
        AuthorizationCriteria criteria = AuthorizationCriteria.builder().requestResources(requestResources).build();

        boolean result = authService.isAuthorized(user, criteria);

        assertTrue(result, "The user should be authorized for a valid facility ID");
    }

    @Test
    void testIsAuthorized_WithValidFacilityId_WithPermission() {

    	final Long facilityId = 1L;
    	final Long accountId = 100L;
        final String permission = "PERMISSION";
        final AppUser user = AppUser.builder().build();
        Map<String, String> requestResources = new HashMap<>(Map.of(CcaResourceType.FACILITY, "1"));
        
        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId))
        	.thenReturn(accountId);
        when(operatorAccountAuthorizationService.isAuthorized(user, accountId, permission)).thenReturn(false);


        AuthorizationCriteria criteria = AuthorizationCriteria.builder()
        		.requestResources(requestResources)
                .permission(permission)
                .build();

        boolean result = authService.isAuthorized(user, criteria);

        assertFalse(result, "The user should be authorized for a valid facility ID");
    }

    @Test
    void isApplicable_ShouldReturnTrue() {

        Map<String, String> requestResources = new HashMap<>(Map.of(CcaResourceType.FACILITY, "1"));
        AuthorizationCriteria criteria = AuthorizationCriteria.builder().requestResources(requestResources).build();
        boolean result = authService.isApplicable(criteria);

        assertTrue(result, "Expected isApplicable to return true when facilityId is not empty.");
    }


    @Test
    void isApplicable_ShouldReturnFalse() {

        AuthorizationCriteria criteria = AuthorizationCriteria.builder().build();
        boolean result = authService.isApplicable(criteria);

        assertFalse(result, "Expected isApplicable to return false when criteria has not facility.");
    }
}
