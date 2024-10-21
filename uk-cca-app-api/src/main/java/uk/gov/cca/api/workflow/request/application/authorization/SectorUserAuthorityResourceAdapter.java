package uk.gov.cca.api.workflow.request.application.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorUserAuthorityResourceService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@Service
@RequiredArgsConstructor
public class SectorUserAuthorityResourceAdapter {

    private final SectorUserAuthorityResourceService sectorUserAuthorityResourceService;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final TargetUnitAccountRepository targetUnitAccountRepository;


    public Map<Long, Set<String>> getUserScopedRequestTaskTypesBySector(AppUser user, Long sectorId) {

        Set<Long> sectorIds = Set.of(sectorId);
        return findTaskTypesPerAccount(user, sectorIds);
    }

    public Map<Long, Set<String>> getUserScopedRequestTaskTypes(AppUser user) {

        Set<Long> sectorIds = sectorAssociationQueryService.getUserSectorAssociations(user);
        return findTaskTypesPerAccount(user, sectorIds);
    }

    private Map<Long, Set<String>> findTaskTypesPerAccount(AppUser user, Set<Long> sectorIds) {
        final Map<Long, Set<String>> requestTaskTypesPerSectorId = findUserScopedRequestTaskTypesBySectorIds(user.getUserId(), sectorIds);
        final Map<Long, Set<String>> requestTaskTypesPerAccount = new HashMap<>();

        for (final Map.Entry<Long, Set<String>> entry : requestTaskTypesPerSectorId.entrySet()) {
            final Long sectorId = entry.getKey();
            final Set<String> taskTypes = entry.getValue();
            final List<Long> accountIds = targetUnitAccountRepository.findAllIdsBySectorAssociationId(sectorId);
            accountIds.forEach(accId -> requestTaskTypesPerAccount.put(accId, taskTypes));
        }
        return requestTaskTypesPerAccount;
    }

    private Map<Long, Set<String>> findUserScopedRequestTaskTypesBySectorIds(String userId, Set<Long> sectorIds) {
        return sectorUserAuthorityResourceService.findUserScopedRequestTaskTypesBySectorAssociationIds(userId, sectorIds);
    }
}