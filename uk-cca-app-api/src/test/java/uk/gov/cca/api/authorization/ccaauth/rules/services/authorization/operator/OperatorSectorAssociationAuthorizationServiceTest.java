package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.operator;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.TargetUnitAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.operator.OperatorAccountAuthorizationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OperatorSectorAssociationAuthorizationServiceTest {

    @Mock
    private TargetUnitAuthorityInfoProvider targetUnitAuthorityInfoProvider;

    @Mock
    private OperatorAccountAuthorizationService operatorAccountAuthorizationService;

    @InjectMocks
    private OperatorSectorAssociationAuthorizationService authService;

    @Test
    void testIsAuthorized_WithValidSectorAssociationId_ShouldReturnTrue() {

        final Long validSectorAssociationId = 1L;
        final AppUser user = AppUser.builder().build();
        final Long accountId = 100L;

        when(targetUnitAuthorityInfoProvider.getAllTargetUnitAccountIdsBySectorAssociationId(validSectorAssociationId))
                .thenReturn(List.of(accountId));
        when(operatorAccountAuthorizationService.isAuthorized(user, accountId)).thenReturn(true);
        Map<String, String> requestResources = new HashMap<>(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "1"));
        AuthorizationCriteria criteria = AuthorizationCriteria.builder().requestResources(requestResources).build();

        boolean result = authService.isAuthorized(user, criteria);

        assertTrue(result, "The user should be authorized for a valid sector association ID");
    }

    @Test
    void testIsAuthorized_WithValidSectorAssociationId_WithPermission() {

        final String permission = "PERMISSION";
        final AppUser user = AppUser.builder().build();

        AuthorizationCriteria criteria = AuthorizationCriteria.builder()
                .permission(permission)
                .build();

        boolean result = authService.isAuthorized(user, criteria);

        assertFalse(result, "The user should be authorized for a valid sector association ID");
    }

    @Test
    void isApplicable_ShouldReturnTrue() {

        Map<String, String> requestResources = new HashMap<>(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "1"));
        AuthorizationCriteria criteria = AuthorizationCriteria.builder().requestResources(requestResources).build();
        boolean result = authService.isApplicable(criteria);

        assertTrue(result, "Expected isApplicable to return true when sectorId is not empty.");
    }


    @Test
    void isApplicable_ShouldReturnFalse() {

        AuthorizationCriteria criteria = AuthorizationCriteria.builder().build();
        boolean result = authService.isApplicable(criteria);

        assertFalse(result, "Expected isApplicable to return false when criteria has not sector association.");
    }
}