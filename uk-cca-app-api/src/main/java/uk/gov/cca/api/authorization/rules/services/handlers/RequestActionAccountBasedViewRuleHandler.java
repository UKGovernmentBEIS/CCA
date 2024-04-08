package uk.gov.cca.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.cca.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.RequestActionAuthorityInfoDTO;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.providers.RequestActionAuthorityInfoProvider;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.RequestAction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("requestActionAccountBasedViewHandler")
@RequiredArgsConstructor
public class RequestActionAccountBasedViewRuleHandler implements AuthorizationResourceRuleHandler {

    private final AppAuthorizationService appAuthorizationService;
    private final RequestActionAuthorityInfoProvider requestActionAuthorityInfoProvider;

    /**
     * Evaluates the {@code authorizationRules} on the {@code resourceId}, which must correspond to an existing {@link RequestAction}.
     *
     * @param authorizationRules the list of
     * @param user the authenticated user
     * @param resourceId the resourceId for which the rules apply.
     */
    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user, String resourceId) {
        RequestActionAuthorityInfoDTO requestActionInfo = requestActionAuthorityInfoProvider.getRequestActionAuthorityInfo(Long.valueOf(resourceId));

        List<AuthorizationRuleScopePermission> appliedRules =
                authorizationRules.stream()
                        .filter(rule -> requestActionInfo.getType().equals(rule.getResourceSubType()))
                        .collect(Collectors.toList());

        if (appliedRules.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        appliedRules.forEach(rule ->
                appAuthorizationService.authorize(user, AuthorizationCriteria.builder()
                        .accountId(requestActionInfo.getAuthorityInfo().getAccountId())
                        .permission(rule.getPermission()).build()));
    }
}