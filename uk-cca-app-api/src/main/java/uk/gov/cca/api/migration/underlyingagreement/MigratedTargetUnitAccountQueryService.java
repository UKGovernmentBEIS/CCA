package uk.gov.cca.api.migration.underlyingagreement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MigratedTargetUnitAccountQueryService {
    
    private final TargetUnitAccountRepository targetUnitAccountRepository;
    private final SectorAssociationRepository sectorAssociationRepository;
    
    public Map<String, TargetUnitAccount> findMigratedAccountsToActivateBySectors(List<String> sectorAcronyms) {
        List<Long> sectorIds = sectorAssociationRepository.findAll()
                .stream()
                .filter(sec -> CollectionUtils.isEmpty(sectorAcronyms) || sectorAcronyms.contains(MigrationUtil.convertLegacyToCcaBusinessId(sec.getAcronym())))
                .map(SectorAssociation::getId)
                .toList();
        
        return targetUnitAccountRepository.findMigratedTargetUnitAccountsByStatusAndSectorIdIn(TargetUnitAccountStatus.NEW, sectorIds).stream()
                .collect(Collectors.toMap(TargetUnitAccount::getBusinessId, acc -> acc));
    }
    
    public Map<String, TargetUnitAccount> findMigratedAccountsToActivateByTargetUnitIds(List<String> legacyTargetUnitIds, List<String> failedEntries) {
        Map<String, TargetUnitAccount> allMigratedAccounts = targetUnitAccountRepository.findMigratedTargetUnitAccountsByStatus(TargetUnitAccountStatus.NEW).stream()
                .collect(Collectors.toMap(TargetUnitAccount::getBusinessId, acc -> acc));

        if (CollectionUtils.isEmpty(legacyTargetUnitIds)) {
            return new HashMap<>(allMigratedAccounts);
        }

        Map<String, TargetUnitAccount> accountsToActivate = allMigratedAccounts.entrySet().stream()
                .filter(entry -> legacyTargetUnitIds.contains(MigrationUtil.convertCcaToLegacyBusinessId(entry.getKey())))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        legacyTargetUnitIds.removeAll(accountsToActivate.keySet().stream().map(MigrationUtil::convertCcaToLegacyBusinessId).toList());
        legacyTargetUnitIds.forEach(notFoundAccount -> failedEntries.add("Target unit account: " + notFoundAccount + " is not included"));
        
        return accountsToActivate;
    }  

}
