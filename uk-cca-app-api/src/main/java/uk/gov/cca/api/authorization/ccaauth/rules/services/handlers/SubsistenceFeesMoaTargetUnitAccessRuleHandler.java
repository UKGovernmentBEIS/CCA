package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsistenceFeesMoaTargetUnitAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

@Service("subsistenceFeesMoaTargetUnitAccessHandler")
@RequiredArgsConstructor
public class SubsistenceFeesMoaTargetUnitAccessRuleHandler implements AuthorizationResourceRuleHandler {

	private final SubsistenceFeesMoaTargetUnitAuthorityInfoProvider subsistenceFeesMoaTargetUnitAuthorityInfoProvider;
    private final AppAuthorizationService appAuthorizationService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user,
                              String resourceId) {

    	final Long accountId = subsistenceFeesMoaTargetUnitAuthorityInfoProvider.getAccountIdByMoaTargetUnitId(Long.parseLong(resourceId));

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
            		.requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
            		.build();
            appAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
