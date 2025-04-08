package uk.gov.cca.api.subsistencefees.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesTransactionIdentifier;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesTransactionIdentifierRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@AllArgsConstructor
public class SubsistenceFeesTransactionIdentifierService {

    private final SubsistenceFeesTransactionIdentifierRepository transactionIdentifierRepository;

    @Transactional
    public Long incrementAndGet(MoaType moaType) {
        SubsistenceFeesTransactionIdentifier identifier = transactionIdentifierRepository.findByMoaType(moaType)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        identifier.setTransactionId(identifier.getTransactionId() + 1);

        return identifier.getTransactionId();
    }
}
