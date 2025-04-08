package uk.gov.cca.api.buyoutsurplus.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.buyoutsurplus.domain.BuyOutTransactionIdentifier;
import uk.gov.cca.api.buyoutsurplus.repository.BuyOutTransactionIdentifierRepository;
import uk.gov.cca.api.buyoutsurplus.util.BuyOutTransactionIdentifierUtil;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.exception.BusinessException;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@AllArgsConstructor
public class BuyOutTransactionIdentifierService {

    private final BuyOutTransactionIdentifierRepository buyOutTransactionIdentifierRepository;

    @Transactional
    public String generateTransactionId(TargetPeriodType targetPeriodType) {
        BuyOutTransactionIdentifier identifier = buyOutTransactionIdentifierRepository.findByTargetPeriodType(targetPeriodType)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        identifier.setTransactionId(identifier.getTransactionId() + 1);

        return BuyOutTransactionIdentifierUtil.generate(targetPeriodType, identifier.getTransactionId());
    }
}
