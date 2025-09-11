package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.BuyOutSurplusTransactionInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

import java.util.Map;
import java.util.Set;

@Service("buyOutSurplusTransactionAccessHandler")
@RequiredArgsConstructor
public class BuyOutSurplusTransactionAccessRuleHandler implements AuthorizationResourceRuleHandler {


	private final AppAuthorizationService appAuthorizationService;
	private final BuyOutSurplusTransactionInfoProvider transactionInfoProvider;

	@Override
	public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules,
	                          AppUser user,
	                          String transactionId) {

		final Long accountId = transactionInfoProvider.getAccountIdByBuyOutSurplusTransactionId(Long.parseLong(transactionId));

		authorizationRules.forEach(rule -> {
			AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
					.requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
					.build();
			appAuthorizationService.authorize(user, authorizationCriteria);
		});
	}
}
