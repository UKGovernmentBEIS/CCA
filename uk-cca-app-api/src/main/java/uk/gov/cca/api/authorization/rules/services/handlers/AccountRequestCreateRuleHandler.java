package uk.gov.cca.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.cca.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Set;

@Service("accountRequestCreateHandler")
@RequiredArgsConstructor
public class AccountRequestCreateRuleHandler implements AuthorizationResourceRuleHandler {
    
    private final AppAuthorizationService appAuthorizationService;
    
    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user,
                              String resourceId) {
        if (authorizationRules.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (resourceId != null) {
            authorizationRules.forEach(rule -> appAuthorizationService.authorize(user,
                    AuthorizationCriteria.builder().accountId(Long.valueOf(resourceId)).permission(rule.getPermission()).build()));
        }
    }
}
