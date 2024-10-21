package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.UnderlyingAgreementAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

@Service("underlyingAgreementAccessHandler")
@RequiredArgsConstructor
public class UnderlyingAgreementAccessRuleHandler implements AuthorizationResourceRuleHandler {

    private final AppAuthorizationService appAuthorizationService;
    private final UnderlyingAgreementAuthorityInfoProvider unaAuthorityInfoProvider;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user, String resourceId) {
        Long accountId = unaAuthorityInfoProvider.getUnderlyingAgreementAccountById(Long.parseLong(resourceId));

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .accountId(accountId)
                    .permission(rule.getPermission())
                    .build();
            
            appAuthorizationService.authorize(user, authorizationCriteria);
        });

    }

}
