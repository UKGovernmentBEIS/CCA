package uk.gov.cca.api.migration.account;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.netz.api.account.service.AccountIdentifierService;
import uk.gov.netz.api.common.exception.BusinessException;

@Service
@AllArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class TargetUnitAccountCreationMigrationService {

    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final TargetUnitAccountService targetUnitAccountService;
    private final AccountIdentifierService accountIdentifierService;
    
    @Transactional
    public void createMigratedTargetUnitAccount(TargetUnitAccountDTO targetUnitDTO) throws Exception {

        if (targetUnitAccountQueryService.isExistingTargetUnitAccount(targetUnitDTO.getBusinessId())) {
            throw new BusinessException(CcaErrorCode.TARGET_UNIT_ACCOUNT_ALREADY_EXISTS, targetUnitDTO.getBusinessId());
        }
        
        final Long accountId = accountIdentifierService.incrementAndGet();

        targetUnitAccountService.createTargetUnitAccount(targetUnitDTO, accountId, targetUnitDTO.getBusinessId());
     }

}
