package uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers;

public interface BuyOutSurplusTransactionInfoProvider {
	Long getAccountIdByBuyOutSurplusTransactionId(Long transactionId);
}
