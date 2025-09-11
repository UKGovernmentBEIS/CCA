package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsistenceFeesMoaFacilityAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

import java.util.Map;
import java.util.Set;

@Service("subsistenceFeesMoaFacilityAccessHandler")
@RequiredArgsConstructor
public class SubsistenceFeesMoaFacilityAccessRuleHandler implements AuthorizationResourceRuleHandler {

    private final SubsistenceFeesMoaFacilityAuthorityInfoProvider subsistenceFeesMoaFacilityAuthorityInfoProvider;
    private final AppAuthorizationService appAuthorizationService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user,
                              String resourceId) {

        final Long accountId = subsistenceFeesMoaFacilityAuthorityInfoProvider.getAccountIdByMoaFacilityId(Long.parseLong(resourceId));

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
                    .build();
            appAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
