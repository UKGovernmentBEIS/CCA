package uk.gov.cca.api.authorization.ccaauth.rules.services.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.netz.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SectorAssociationAuthorizationResourceService {

    private final ResourceScopePermissionService resourceScopePermissionService;
    private final AppAuthorizationService appAuthorizationService;

    public boolean hasUserScopeToSectorAssociation(AppUser authUser, String scope, Long sectorAssociationId) {
        String requiredPermission =
            resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(CcaResourceType.SECTOR_ASSOCIATION,
                    authUser.getRoleType(), scope)
                .map(ResourceScopePermission::getPermission)
                .orElse(null);
        if(requiredPermission == null) {
            return false;
        }
        try {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()))
                    .permission(requiredPermission)
                    .build();
            appAuthorizationService.authorize(authUser, authorizationCriteria);
        } catch (BusinessException e) {
            return false;
        }

        return true;
    }
}
