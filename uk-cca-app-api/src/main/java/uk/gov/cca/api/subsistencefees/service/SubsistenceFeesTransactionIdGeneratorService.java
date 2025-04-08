package uk.gov.cca.api.subsistencefees.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.subsistencefees.SubsistenceFeesTransactionIdentifierUtil;
import uk.gov.cca.api.subsistencefees.domain.MoaType;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesTransactionIdGeneratorService {

    private final SubsistenceFeesTransactionIdentifierService transactionIdentifierService;

    @Transactional
    public String generateTransactionIdForSectorMOAs() {
        final Long identifier = transactionIdentifierService.incrementAndGet(MoaType.SECTOR_MOA);
        return SubsistenceFeesTransactionIdentifierUtil.generate(MoaType.SECTOR_MOA.getMoaTypeIdentifier(), identifier);
    }

    @Transactional
    public String generateTransactionIdForTargetUnitMOAs() {
        final Long identifier = transactionIdentifierService.incrementAndGet(MoaType.TARGET_UNIT_MOA);
        return SubsistenceFeesTransactionIdentifierUtil.generate(MoaType.TARGET_UNIT_MOA.getMoaTypeIdentifier(), identifier);
    }
}
