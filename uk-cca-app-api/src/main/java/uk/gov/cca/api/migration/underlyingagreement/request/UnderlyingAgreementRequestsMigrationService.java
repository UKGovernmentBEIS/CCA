package uk.gov.cca.api.migration.underlyingagreement.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;
import uk.gov.netz.api.common.utils.ExceptionUtils;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class UnderlyingAgreementRequestsMigrationService extends MigrationBaseService {

    private final TargetUnitAccountRepository targetUnitAccountRepository;
    private final UnderlyingAgreementRequestMigrationService underlyingAgreementRequestMigrationService;
    @Override
    public String getResource() {
        return "underlying-agreement-requests";
    }

    @Override
    public List<String> migrate(String ids) {
        List<String> migrationResults = new ArrayList<>();
        
        List<TargetUnitAccount> migratedAccounts = targetUnitAccountRepository.findMigratedTargetUnitAccountsByStatus(TargetUnitAccountStatus.LIVE);
        
        if (!StringUtils.isBlank(ids)) {
            List<String> legalTargetUnits = new ArrayList<>(Arrays.asList(ids.split("\\s*,\\s*")));

            migratedAccounts = migratedAccounts.stream()
                    .filter(acc -> legalTargetUnits.contains(MigrationUtil.convertCcaToLegacyBusinessId(acc.getBusinessId())))
                    .toList();

            if (CollectionUtils.isEmpty(migratedAccounts)) {
                return List.of("No data found");
            }
        }
        
        AtomicInteger failedCounter = new AtomicInteger(0);
        LocalDateTime migrationDate = LocalDateTime.now();

        for (TargetUnitAccount account : migratedAccounts) {    
            try {                
                underlyingAgreementRequestMigrationService.migrateRequest(migrationDate, account);
                migrationResults.add(constructSuccessMessage(account.getBusinessId()));
            } catch (Exception ex) {
                failedCounter.incrementAndGet();
                log.error("migration of request for account : {} failed with {}", account.getBusinessId(),
                        ExceptionUtils.getRootCause(ex).getMessage());

                migrationResults.add(constructErrorMessage(account,
                        ExceptionUtils.getRootCause(ex).getMessage(), null));
            }
        }
        
        migrationResults.add("Results: " + failedCounter.get() + "/" + migratedAccounts.size() + " failed");
        return migrationResults;
    }
    
    public static String constructErrorMessage(TargetUnitAccount account, String errorMessage, String data) {
        return "target_unit_id: " + MigrationUtil.convertLegacyToCcaBusinessId(account.getBusinessId())
        + " | Error: " + errorMessage
        + " | data: " + data;
    }

    public static String constructSuccessMessage(String targetUnitId) {
        return "target_unit_id: " + MigrationUtil.convertLegacyToCcaBusinessId(targetUnitId);
    }

}
