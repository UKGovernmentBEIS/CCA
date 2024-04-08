package uk.gov.cca.api.authorization.rules.services.handlers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.Account;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.cca.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.RequestAuthorityInfoDTO;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.providers.RequestAuthorityInfoProvider;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Set;

@Service("requestAccessHandler")
@RequiredArgsConstructor
public class RequestAccessRuleHandler implements AuthorizationResourceRuleHandler {
    private final AppAuthorizationService appAuthorizationService;
    private final RequestAuthorityInfoProvider requestAuthorityInfoProvider;

    /**
     * @param user the authenticated user
     * @param authorizationRules the list of
     * @param resourceId the resourceId for which the rules apply.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     *
     * Authorizes access on {@link Account} or {@link CompetentAuthorityEnum}
     * of {@link Request} with id the {@code resourceId}
     * and permission the permission of the rule
     */
    @Override
    public void evaluateRules(@Valid Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user, String resourceId) {
        RequestAuthorityInfoDTO requestInfoDTO = requestAuthorityInfoProvider.getRequestInfo(resourceId);
        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .accountId(requestInfoDTO.getAuthorityInfo().getAccountId())
                    .competentAuthority(requestInfoDTO.getAuthorityInfo().getCompetentAuthority())
                    .verificationBodyId(requestInfoDTO.getAuthorityInfo().getVerificationBodyId())
                    .permission(rule.getPermission())
                    .build();
            appAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
