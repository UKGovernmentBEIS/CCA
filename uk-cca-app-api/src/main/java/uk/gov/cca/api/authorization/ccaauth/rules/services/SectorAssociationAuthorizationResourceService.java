package uk.gov.cca.api.authorization.ccaauth.rules.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.netz.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.netz.api.common.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class SectorAssociationAuthorizationResourceService {

    private final ResourceScopePermissionService resourceScopePermissionService;
    private final SectorAssociationAuthorizationService sectorAssociationAuthorizationService;

    public boolean hasUserScopeToSectorAssociation(AppUser authUser, String scope, Long sectorAssociationId) {
        String requiredPermission =
            resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(CcaResourceType.SECTOR_ASSOCIATION,
                    authUser.getRoleType(), scope)
                .map(ResourceScopePermission::getPermission)
                .orElse(null);
        try {
            sectorAssociationAuthorizationService.authorize(authUser, sectorAssociationId, requiredPermission);
        } catch (BusinessException e) {
            return false;
        }

        return true;
    }
}
