package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.SectorAssociationAuthorizationService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Set;

@Service("sectorAssociationRequestCreateHandler")
@RequiredArgsConstructor
public class SectorAssociationRequestCreateRuleHandler implements AuthorizationResourceRuleHandler {

    private final SectorAssociationAuthorizationService sectorAssociationAuthorizationService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user, String resourceId) {
        if (authorizationRules.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (resourceId != null) {
            authorizationRules.forEach(rule -> sectorAssociationAuthorizationService.authorize(user,Long.parseLong(resourceId),rule.getPermission()));
        }
    }
}
