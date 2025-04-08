package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsistenceFeesRunAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Service("subsistenceFeesRunAccessHandler")
@RequiredArgsConstructor
public class SubsistenceFeesRunAccessRuleHandler implements AuthorizationResourceRuleHandler {

	private final SubsistenceFeesRunAuthorityInfoProvider subsistenceFeesRunAuthorityInfoProvider;
    private final AppAuthorizationService appAuthorizationService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user,
                              String resourceId) {

        CompetentAuthorityEnum competentAuthority = subsistenceFeesRunAuthorityInfoProvider.getSubsistenceFeesRunCaById(Long.parseLong(resourceId));

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
            		.requestResources(Map.of(ResourceType.CA, competentAuthority.name()))
            		.build();
            appAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
