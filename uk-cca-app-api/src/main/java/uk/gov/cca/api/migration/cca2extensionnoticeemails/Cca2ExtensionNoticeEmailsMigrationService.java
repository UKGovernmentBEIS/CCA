package uk.gov.cca.api.migration.cca2extensionnoticeemails;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca2ExtensionNoticeEmailsMigrationService extends MigrationBaseService {

    private final RequestQueryService requestQueryService;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final Cca2ExtensionNoticeMigrationService cca2ExtensionNoticeMigrationService;

    @Override
    @Transactional(readOnly = true)
    public List<String> migrate(String ids) {
        Set<String> accountBusinessIds = StringUtils.isBlank(ids)
                ? Set.of() : Arrays.stream(ids.split(",")).collect(Collectors.toSet());

        List<TargetUnitAccountBusinessInfoDTO> activeAccounts = accountBusinessIds.isEmpty()
                ? targetUnitAccountQueryService.getActiveAccounts()
                : targetUnitAccountQueryService.getActiveAccountsByBusinessIds(accountBusinessIds);

        Set<String> diff = SetUtils.difference(accountBusinessIds, activeAccounts.stream().map(TargetUnitAccountBusinessInfoDTO::getBusinessId).collect(Collectors.toSet()));
        if(!diff.isEmpty()) {
            return List.of(String.format("The process was interrupted because the following accounts are not live: %s", diff));
        }

        Set<String> accountsProcessed = new LinkedHashSet<>();

        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            List<ScheduledFuture<String>> allFutures = new ArrayList<>();
            AtomicInteger delay = new AtomicInteger();

            for(TargetUnitAccountBusinessInfoDTO account: activeAccounts) {
                List<Request> result = requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                        CcaRequestType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING, ResourceType.ACCOUNT, String.valueOf(account.getAccountId()));

                if(!result.isEmpty()) {
                    result.getFirst().getRequestActions().stream()
                            .filter(action -> action.getType().equals(CcaRequestActionType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED))
                            .findFirst().ifPresent(requestAction ->
                                    allFutures.add(executor.schedule(() -> cca2ExtensionNoticeMigrationService
                                            .sendEmail(requestAction, account.getBusinessId()), delay.getAndIncrement(), TimeUnit.SECONDS)));
                }
            }

            for(ScheduledFuture<String> future : allFutures) {
                accountsProcessed.add(future.get());
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return List.of(String.format("CCA2 extension notice emails sent. Accounts [%s] processed: %s", accountsProcessed.size(), accountsProcessed));
    }

    @Override
    public String getResource() {
        return "cca2-extension-notice-emails";
    }
}
