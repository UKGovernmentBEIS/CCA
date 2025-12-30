package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service("facilityRequestCreateHandler")
@RequiredArgsConstructor
public class FacilityRequestCreateRuleHandler implements AuthorizationResourceRuleHandler {

	private final AppAuthorizationService appAuthorizationService;
    private final AuthorizationRulesQueryService authorizationRulesQueryService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user, String resourceId) {
        if (authorizationRules.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        final Set<String> userAllowedRequestTypes = authorizationRulesQueryService
                .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST, user.getRoleType());

        Set<String> ruleRequestTypes = authorizationRules.stream()
                .map(AuthorizationRuleScopePermission::getResourceSubType)
                .collect(Collectors.toSet());

        if (!userAllowedRequestTypes.containsAll(ruleRequestTypes)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (resourceId != null) {
            authorizationRules.forEach(rule -> {
                AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                		.requestResources(Map.of(CcaResourceType.FACILITY, resourceId))
                        .permission(rule.getPermission())
                        .build();
                appAuthorizationService.authorize(user, authorizationCriteria);
            });
        }
    }
}
