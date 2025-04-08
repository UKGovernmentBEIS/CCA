package uk.gov.cca.api.authorization.ccaauth.rules.services.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.RequestTaskRoleTypeAuthorizationQueryService;
import uk.gov.netz.api.authorization.rules.services.resource.ResourceCriteria;

import java.util.List;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class SectorUserRequestTaskRoleTypeAuthorizationQueryService implements RequestTaskRoleTypeAuthorizationQueryService {

    private final SectorUserAuthorityService sectorUserAuthorityService;
    private final SectorUserAuthorityResourceService sectorUserAuthorityResourceService;

    @Override
    public List<String> findUsersWhoCanExecuteRequestTaskTypeByResourceCriteria(
            String requestTaskType, ResourceCriteria resourceCriteria, boolean requiresPermission) {

        Long accountSectorAssociationId = 
        		Long.parseLong(resourceCriteria.getRequestResources().get(CcaResourceType.SECTOR_ASSOCIATION));

        if (!requiresPermission) {
            return sectorUserAuthorityService.findActiveSectorUsersBySectorAssociationId(accountSectorAssociationId);
        } else {
            return sectorUserAuthorityResourceService.
                    findSectorUsersWithScopeOnResourceTypeAndSubTypeAndSectorAssociationId(
                            ResourceType.REQUEST_TASK, requestTaskType, Scope.REQUEST_TASK_EXECUTE, accountSectorAssociationId);
        }
    }

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }
}
