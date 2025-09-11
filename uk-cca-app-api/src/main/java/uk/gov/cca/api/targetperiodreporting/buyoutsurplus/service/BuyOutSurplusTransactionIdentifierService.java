package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionIdentifier;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionIdentifierRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.util.BuyOutTransactionIdentifierUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.exception.BusinessException;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@AllArgsConstructor
public class BuyOutSurplusTransactionIdentifierService {

    private final BuyOutSurplusTransactionIdentifierRepository buyOutSurplusTransactionIdentifierRepository;

    @Transactional
    public String generateTransactionCode(TargetPeriodType targetPeriodType) {
        BuyOutSurplusTransactionIdentifier identifier = buyOutSurplusTransactionIdentifierRepository.findByTargetPeriodType(targetPeriodType)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        identifier.setTransactionId(identifier.getTransactionId() + 1);

        return BuyOutTransactionIdentifierUtil.generate(targetPeriodType, identifier.getTransactionId());
    }
}
