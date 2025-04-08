package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.UnderlyingAgreementAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementAccessRuleHandlerTest {
    @InjectMocks
    private UnderlyingAgreementAccessRuleHandler underlyingAgreementAccessRuleHandler;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Mock
    private UnderlyingAgreementAuthorityInfoProvider underlyingAgreementAuthorityInfoProvider;

    @Test
    void evaluateRules() {
        Long accountId = 1L;
        Long underlyingAgreementId = 2L;
        AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
                .handler("underlyingAgreementAccessHandler")
                .permission("permission1")
                .build();

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
                .permission(rule.getPermission())
                .build();

        when(underlyingAgreementAuthorityInfoProvider.getUnderlyingAgreementAccountById(underlyingAgreementId)).thenReturn(accountId);

        underlyingAgreementAccessRuleHandler.evaluateRules(Set.of(rule), user, String.valueOf(underlyingAgreementId));

        verify(underlyingAgreementAuthorityInfoProvider, times(1)).getUnderlyingAgreementAccountById(underlyingAgreementId);
        verify(appAuthorizationService, times(1)).authorize(user, authorizationCriteria);
    }
}