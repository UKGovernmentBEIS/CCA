package uk.gov.cca.api.authorization.ccaauth.rules.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.netz.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@ExtendWith(MockitoExtension.class)
public class SectorAssociationAuthorizationResourceServiceTest {

    @InjectMocks
    private SectorAssociationAuthorizationResourceService service;

    @Mock
    private ResourceScopePermissionService resourceScopePermissionService;

    @Mock
    private SectorAssociationAuthorizationService sectorAssociationAuthorizationService;

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

        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(CcaResourceType.SECTOR_ASSOCIATION, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));

        boolean result = service.hasUserScopeToSectorAssociation(authUser, scope, sectorAssociationId);

        assertThat(result).isTrue();
        verify(resourceScopePermissionService, times(1))
            .findByResourceTypeAndRoleTypeAndScope(CcaResourceType.SECTOR_ASSOCIATION, roleType, scope);
        verify(sectorAssociationAuthorizationService, times(1))
            .authorize(Mockito.eq(authUser), Mockito.eq(sectorAssociationId), anyString());
    }
}
