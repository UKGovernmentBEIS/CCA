package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountIdentifierService;
import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.cca.api.account.util.TargetUnitAccountBusinessIdGeneratorUtil;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.netz.api.account.service.AccountIdentifierService;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountCreationService {

    private final TargetUnitAccountService targetUnitAccountService;
    private final TargetUnitAccountIdentifierService targetUnitAccountIdentifierService;
    private final AccountIdentifierService accountIdentifierService;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final TargetUnitAccountCreationValidationService targetUnitAccountCreationValidationService;

    @Transactional
    public TargetUnitAccountDTO createAccount(TargetUnitAccountDTO accountDTO) {
        // Validate
        targetUnitAccountCreationValidationService.validate(accountDTO);

        // Generate identifiers
        final Long identifier = targetUnitAccountIdentifierService.incrementAndGet(accountDTO.getSectorAssociationId());
        final String businessId = generateBusinessId(accountDTO.getSectorAssociationId(), identifier);
        final Long accountId = accountIdentifierService.incrementAndGet();

        // Create account
        return targetUnitAccountService.createTargetUnitAccount(accountDTO, accountId, businessId);
    }

    private String generateBusinessId(Long sectorAssociationId, Long accountId) {
        String acronym = sectorAssociationQueryService.getSectorAssociationAcronymById(sectorAssociationId);
        return TargetUnitAccountBusinessIdGeneratorUtil.generate(acronym, accountId);
    }
}
