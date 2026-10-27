package uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers;

public interface BuyOutSurplusTransactionDetailInfoProvider {
    Long getAccountIdByBuyOutSurplusTransactionDetailId(Long transactionDetailId);
}