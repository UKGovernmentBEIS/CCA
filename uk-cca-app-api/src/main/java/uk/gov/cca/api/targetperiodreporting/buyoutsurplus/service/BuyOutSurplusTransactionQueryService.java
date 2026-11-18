package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.BuyOutSurplusTransactionInfoProvider;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusTransactionQueryService implements BuyOutSurplusTransactionInfoProvider {
    
    private final BuyOutSurplusTransactionRepository repository;
    
    @Override
    public Long getAccountIdByBuyOutSurplusTransactionId(Long transactionId) {
        return repository.findAccountIdByTransactionId(transactionId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
