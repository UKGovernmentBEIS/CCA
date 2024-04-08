package uk.gov.cca.api.authorization.rules.services.resource;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;
import uk.gov.cca.api.authorization.rules.domain.Scope;
import uk.gov.cca.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.exception.BusinessException;

import static uk.gov.cca.api.authorization.rules.domain.ResourceType.CA;

@Service
@RequiredArgsConstructor
public class CompAuthAuthorizationResourceService {
    
    private final ResourceScopePermissionService resourceScopePermissionService;
    private final AppAuthorizationService appAuthorizationService;

    public boolean hasUserScopeToCompAuth(AppUser authUser, String scope) {
        String requiredPermission =
                resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(CA, authUser.getRoleType(), scope)
                .map(ResourceScopePermission::getPermission)
                .orElse(null);
        
        AuthorizationCriteria authCriteria = 
                AuthorizationCriteria.builder()
                    .competentAuthority(authUser.getCompetentAuthority())
                    .permission(requiredPermission).build();
        try {
            appAuthorizationService.authorize(authUser, authCriteria);
        } catch (BusinessException e) {
            return false;
        }
        
        return true;
    }
    
    public boolean hasUserScopeOnResourceSubType(AppUser authUser, String scope, String resourceSubType) {
        String requiredPermission =
                resourceScopePermissionService
                        .findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(CA,
                                resourceSubType, authUser.getRoleType(), scope)
                .map(ResourceScopePermission::getPermission)
                .orElse(null);
        
        AuthorizationCriteria authCriteria = 
                AuthorizationCriteria.builder()
                    .competentAuthority(authUser.getCompetentAuthority())
                    .permission(requiredPermission).build();
        try {
            appAuthorizationService.authorize(authUser, authCriteria);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }
}
