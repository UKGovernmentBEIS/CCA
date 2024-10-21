package uk.gov.cca.api.authorization.ccaauth.rules.services.resource;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.domain.Scope;

@Service
@RequiredArgsConstructor
public class SectorUserAuthorityResourceService {

	private final CcaAuthorityRepository authorityRepository;
	
	public Map<Long, Set<String>> findUserScopedRequestTaskTypesBySectorAssociationIds(String userId, Set<Long> sectorIds) {
        return authorityRepository
            .findSectorUserAssignedResourceSubTypesBySectorAssociations(userId, sectorIds, ResourceType.REQUEST_TASK, Scope.REQUEST_TASK_VIEW);
    }

    public List<String> findSectorUsersWithScopeOnResourceTypeAndSubTypeAndSectorAssociationId(
            String resourceType, String resourceSubType, String scope, Long sectorAssociationId) {
        return authorityRepository.findSectorUsersWithScopeOnResourceTypeAndSubTypeAndSectorAssociationId(
                resourceType, resourceSubType, scope, sectorAssociationId);
    }
}
