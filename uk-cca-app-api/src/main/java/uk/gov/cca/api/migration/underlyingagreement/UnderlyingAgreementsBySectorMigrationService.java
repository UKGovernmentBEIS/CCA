package uk.gov.cca.api.migration.underlyingagreement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class UnderlyingAgreementsBySectorMigrationService extends MigrationBaseService {
    
    private final MigratedTargetUnitAccountQueryService migratedTargetUnitAccountQueryService;
    private final UnderlyingAgreementBuilderService underlyingAgreementBuilderService;
    private final UnderlyingAgreementMigrationService underlyingAgreementMigrationService;

    @Override
    public String getResource() {
        return "underlying-agreements";
    }
    
    @Override
    public List<String> migrate(String ids) {
        List<String> sectorAcronyms = !StringUtils.isBlank(ids)
                ? new ArrayList<>(Arrays.asList(ids.split("\\s*,\\s*")))
                : new ArrayList<>();
        
        Map<String, TargetUnitAccount> migratedAccountsToActivate = migratedTargetUnitAccountQueryService.findMigratedAccountsToActivateBySectors(sectorAcronyms);
        if(migratedAccountsToActivate != null && migratedAccountsToActivate.size() > 2000) {
            return List.of("ERROR: There are too many results. Please narrow down the results with additional criteria.");
        }
               
        Map<String, UnderlyingAgreementMigrationContainer> underlyingAgreements =
                underlyingAgreementBuilderService.buildUnderlyingAgreements(migratedAccountsToActivate);
        
        int failedCounter = 0;
        List<String> migrationResults = new ArrayList<>();
        for (Map.Entry<String, UnderlyingAgreementMigrationContainer> entry : underlyingAgreements.entrySet()) {
            try {
                underlyingAgreementMigrationService.migrateUnderlyingAgreement(entry.getKey(), entry.getValue(), migrationResults);
            } catch (Exception e) {
                log.error(e.getMessage());
                failedCounter++;
            }
        }
        
        migrationResults.add("Migration of underlying agreement results: " + failedCounter + "/" + underlyingAgreements.size() + " failed");
        return migrationResults;
    }

}
