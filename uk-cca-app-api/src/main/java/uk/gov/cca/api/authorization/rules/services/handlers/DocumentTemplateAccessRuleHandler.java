package uk.gov.cca.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.cca.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.providers.DocumentTemplateAuthorityInfoProvider;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Set;

@Service("documentTemplateAccessHandler")
@RequiredArgsConstructor
public class DocumentTemplateAccessRuleHandler implements AuthorizationResourceRuleHandler {

    private final DocumentTemplateAuthorityInfoProvider templateAuthorityInfoProvider;
    private final AppAuthorizationService appAuthorizationService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user,
                              String resourceId) {

        CompetentAuthorityEnum competentAuthority = templateAuthorityInfoProvider.getDocumentTemplateCaById(Long.parseLong(resourceId));

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .competentAuthority(competentAuthority)
                .build();
            appAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
