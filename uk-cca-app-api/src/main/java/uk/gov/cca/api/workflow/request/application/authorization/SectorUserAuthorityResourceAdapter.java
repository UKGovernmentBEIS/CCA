package uk.gov.cca.api.workflow.request.application.authorization;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorUserAuthorityResourceService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@Service
@RequiredArgsConstructor
public class SectorUserAuthorityResourceAdapter {

    private final SectorUserAuthorityResourceService sectorUserAuthorityResourceService;
    private final SectorAssociationQueryService sectorAssociationQueryService;


    public Map<Long, Set<String>> getUserScopedRequestTaskTypesBySector(AppUser user, Long sectorId) {

        Set<Long> sectorIds = Set.of(sectorId);
        return findUserScopedRequestTaskTypesBySectorIds(user.getUserId(), sectorIds);
    }

    public Map<Long, Set<String>> getUserScopedRequestTaskTypes(AppUser user) {

        Set<Long> sectorIds = sectorAssociationQueryService.getUserSectorAssociationIds(user);
        return findUserScopedRequestTaskTypesBySectorIds(user.getUserId(), sectorIds);
    }

    private Map<Long, Set<String>> findUserScopedRequestTaskTypesBySectorIds(String userId, Set<Long> sectorIds) {
        return sectorUserAuthorityResourceService.findUserScopedRequestTaskTypesBySectorAssociationIds(userId, sectorIds);
    }
}