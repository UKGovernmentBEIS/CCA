package uk.gov.cca.api.authorization.rules.services.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;
import uk.gov.cca.api.authorization.rules.domain.Scope;
import uk.gov.cca.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyAuthorizationResourceServiceTest {

    @InjectMocks
    private VerificationBodyAuthorizationResourceService service;
    
    @Mock
    private ResourceScopePermissionService resourceScopePermissionService;
    
    @Mock
    private AppAuthorizationService appAuthorizationService;
    
    @Test
    void hasUserScopeToVerificationBody() {
    	RoleType roleType = RoleType.VERIFIER;
    	AppUser authUser = AppUser.builder().roleType(roleType).build();
        Long verificationBodyId = 1L;
        String scope = Scope.EDIT_USER;
        
        ResourceScopePermission resourceScopePermission = 
                ResourceScopePermission.builder().permission(Permission.PERM_CA_USERS_EDIT).build();
        
        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.VERIFICATION_BODY, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));
        
        boolean result = service.hasUserScopeToVerificationBody(authUser, verificationBodyId, scope);
        
        assertThat(result).isTrue();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndRoleTypeAndScope(ResourceType.VERIFICATION_BODY, roleType, scope);
        verify(appAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getVerificationBodyId()).isEqualTo(verificationBodyId);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }
    
    @Test
    void hasUserScopeToVerificationBody_not_authorized() {
    	RoleType roleType = RoleType.VERIFIER;
    	AppUser authUser = AppUser.builder().roleType(roleType).build();
        Long verificationBodyId = 1L;
        String scope = Scope.EDIT_USER;
        
        ResourceScopePermission resourceScopePermission = 
                ResourceScopePermission.builder().permission(Permission.PERM_CA_USERS_EDIT).build();
        
        AuthorizationCriteria authCriteria = 
                AuthorizationCriteria.builder()
                    .verificationBodyId(verificationBodyId)
                    .permission(Permission.PERM_CA_USERS_EDIT).build();
        
        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.VERIFICATION_BODY, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));
        
        doThrow(BusinessException.class).when(appAuthorizationService).authorize(authUser, authCriteria);
        
        boolean result = service.hasUserScopeToVerificationBody(authUser, verificationBodyId, scope);
        
        assertThat(result).isFalse();
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndRoleTypeAndScope(ResourceType.VERIFICATION_BODY, roleType, scope);
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(appAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getVerificationBodyId()).isEqualTo(verificationBodyId);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }
}
