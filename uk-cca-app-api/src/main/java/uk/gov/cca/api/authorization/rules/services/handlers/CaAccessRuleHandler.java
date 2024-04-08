package uk.gov.cca.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.cca.api.authorization.rules.services.AuthorizationRuleHandler;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;

import java.util.Set;

@Service("caAccessHandler")
@RequiredArgsConstructor
public class CaAccessRuleHandler implements AuthorizationRuleHandler {

    private final AppAuthorizationService appAuthorizationService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user) {

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .permission(rule.getPermission())
                    .competentAuthority(user.getCompetentAuthority())
                    .build();
            appAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
