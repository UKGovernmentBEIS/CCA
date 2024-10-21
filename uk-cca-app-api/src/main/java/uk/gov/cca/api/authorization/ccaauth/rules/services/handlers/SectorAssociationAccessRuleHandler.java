package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.SectorAssociationAuthorizationService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Set;

@Service("sectorAssociationAccessHandler")
@RequiredArgsConstructor
public class SectorAssociationAccessRuleHandler implements AuthorizationResourceRuleHandler {

    private final SectorAssociationAuthorizationService sectorAssociationAuthorizationService;

    /**
     * @param user the authenticated user
     * @param authorizationRules the list of
     * @param resourceId the resourceId for which the rules apply.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     *
     * Authorizes access on {@link uk.gov.cca.api.sectorassociation.domain.SectorAssociation}
     * with id the {@code resourceId} and permission the permission of the rule
     */
    @Override
    public void evaluateRules(@Valid Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user, String resourceId) {
        authorizationRules.forEach(
            rule -> sectorAssociationAuthorizationService.authorize(user, Long.parseLong(resourceId), rule.getPermission())
        );
    }
}
