package uk.gov.cca.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.TargetUnitAccountIdentifier;
import uk.gov.cca.api.account.repository.TargetUnitAccountIdentifierRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountIdentifierService {

    private final TargetUnitAccountIdentifierRepository targetUnitAccountIdentifierRepository;

    @Transactional
    public Long incrementAndGet(Long sectorAssociationId) {
        TargetUnitAccountIdentifier identifier = targetUnitAccountIdentifierRepository.findTargetUnitAccountIdentifierBySectorAssociationId(sectorAssociationId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        // Increment identifier
        identifier.setAccountId(identifier.getAccountId() + 1);

        return identifier.getAccountId();
    }
}