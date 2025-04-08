package uk.gov.cca.api.authorization.ccaauth.rules.services.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.netz.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class SectorAssociationAuthorizationResourceServiceTest {

    @InjectMocks
    private SectorAssociationAuthorizationResourceService service;

    @Mock
    private ResourceScopePermissionService resourceScopePermissionService;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Test
    void hasUserScopeToSectorAssociation() {
        Long sectorAssociationId = 1L;
        String roleType = REGULATOR;
        CompetentAuthorityEnum compAuth = CompetentAuthorityEnum.ENGLAND;
        AppUser authUser = AppUser.builder()
            .authorities(List.of(AppAuthority.builder().competentAuthority(compAuth).build()))
            .roleType(roleType).build();

        String scope = CcaScope.EDIT_SECTOR_ASSOCIATION;

        ResourceScopePermission resourceScopePermission =
            ResourceScopePermission.builder().permission(CcaPermission.PERM_SECTOR_ASSOCIATION_EDIT).build();

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()))
                .permission(CcaPermission.PERM_SECTOR_ASSOCIATION_EDIT)
                .build();

        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(CcaResourceType.SECTOR_ASSOCIATION, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));

        boolean result = service.hasUserScopeToSectorAssociation(authUser, scope, sectorAssociationId);

        assertThat(result).isTrue();
        verify(resourceScopePermissionService, times(1))
            .findByResourceTypeAndRoleTypeAndScope(CcaResourceType.SECTOR_ASSOCIATION, roleType, scope);
        verify(appAuthorizationService, times(1))
            .authorize(authUser, authorizationCriteria);
    }

    @Test
    void hasUserScopeToSectorAssociation_Null_requiredPermission() {
        Long sectorAssociationId = 1L;
        String roleType = OPERATOR;
        AppUser authUser = AppUser.builder()
                .roleType(roleType).build();
        String scope = CcaScope.EDIT_SECTOR_ASSOCIATION;

        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(
                CcaResourceType.SECTOR_ASSOCIATION, roleType, scope))
                .thenReturn(Optional.empty());

        boolean result = service.hasUserScopeToSectorAssociation(authUser, scope, sectorAssociationId);

        assertThat(result).isFalse();
    }

}
