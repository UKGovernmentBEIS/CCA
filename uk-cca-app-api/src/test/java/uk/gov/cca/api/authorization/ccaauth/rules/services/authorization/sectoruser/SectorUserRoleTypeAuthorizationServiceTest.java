package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.sectoruser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SectorUserRoleTypeAuthorizationServiceTest {

    private final SectorUserResourceTypeAuthorizationService sectorUserResourceTypeAuthorizationService = mock(SectorUserResourceTypeAuthorizationService.class);
    private final List<SectorUserResourceTypeAuthorizationService> sectorUserResourceTypeAuthorizationServices = Collections.singletonList(sectorUserResourceTypeAuthorizationService);
    private final SectorUserRoleTypeAuthorizationService sectorUserRoleTypeAuthorizationService = new SectorUserRoleTypeAuthorizationService(sectorUserResourceTypeAuthorizationServices);

    @Test
    void isAuthorized_true() {
        AppUser user = AppUser.builder().roleType(CcaRoleTypeConstants.SECTOR_USER).build();
        AuthorizationCriteria criteria = AuthorizationCriteria.builder()
                .requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "2", ResourceType.ACCOUNT, "1"))
                .build();

        when(sectorUserResourceTypeAuthorizationService.isApplicable(criteria)).thenReturn(true);
        when(sectorUserResourceTypeAuthorizationService.isAuthorized(user, criteria)).thenReturn(true);

        assertTrue(sectorUserRoleTypeAuthorizationService.isAuthorized(user, criteria));

        verify(sectorUserResourceTypeAuthorizationService, times(1)).isApplicable(criteria);
        verify(sectorUserResourceTypeAuthorizationService, times(1)).isAuthorized(user, criteria);
    }

    @Test
    void isAuthorized_false() {
        AppUser user = AppUser.builder().roleType(CcaRoleTypeConstants.SECTOR_USER).build();
        AuthorizationCriteria criteria = AuthorizationCriteria.builder()
                .requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "2", ResourceType.ACCOUNT, "1"))
                .build();

        when(sectorUserResourceTypeAuthorizationService.isApplicable(criteria)).thenReturn(true);
        when(sectorUserResourceTypeAuthorizationService.isAuthorized(user, criteria)).thenReturn(false);

        Assertions.assertFalse(sectorUserRoleTypeAuthorizationService.isAuthorized(user, criteria));

        verify(sectorUserResourceTypeAuthorizationService, times(1)).isApplicable(criteria);
        verify(sectorUserResourceTypeAuthorizationService, times(1)).isAuthorized(user, criteria);
    }

    @Test
    void isAuthorized_no_applicable_resource_service() {
        AppUser user = AppUser.builder().roleType(CcaRoleTypeConstants.SECTOR_USER).build();
        AuthorizationCriteria criteria = AuthorizationCriteria.builder()
                .requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "2", ResourceType.ACCOUNT, "1"))
                .build();

        when(sectorUserResourceTypeAuthorizationService.isApplicable(criteria)).thenReturn(false);

        Assertions.assertFalse(sectorUserRoleTypeAuthorizationService.isAuthorized(user, criteria));

        verify(sectorUserResourceTypeAuthorizationService, times(1)).isApplicable(criteria);
    }

    @Test
    void getRoleType() {
        Assertions.assertEquals(CcaRoleTypeConstants.SECTOR_USER, sectorUserRoleTypeAuthorizationService.getRoleType());
    }

}