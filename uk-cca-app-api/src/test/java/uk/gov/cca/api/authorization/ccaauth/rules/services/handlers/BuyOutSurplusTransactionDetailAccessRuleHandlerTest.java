package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.BuyOutSurplusTransactionDetailInfoProvider;
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
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionDetailAccessRuleHandlerTest {

    @InjectMocks
    private BuyOutSurplusTransactionDetailAccessRuleHandler handler;

    @Mock
    private BuyOutSurplusTransactionDetailInfoProvider provider;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Test
    void evaluateRules() {

        String transactionId = String.valueOf(99L);
        long accountId = 1L;
        AppUser user = AppUser.builder().roleType(REGULATOR).build();
        AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
                .handler("buyOutSurplusTransactionDetailAccessHandler")
                .build();

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .requestResources(Map.of(ResourceType.ACCOUNT, Long.toString(accountId)))
                .permission(rule.getPermission())
                .build();

        when(provider.getAccountIdByBuyOutSurplusTransactionDetailId(Long.parseLong(transactionId)))
                .thenReturn(accountId);

        // Invoke
        handler.evaluateRules(Set.of(rule), user, transactionId);

        // Verify
        verify(appAuthorizationService, times(1)).authorize(user, authorizationCriteria);
        verify(provider, times(1))
                .getAccountIdByBuyOutSurplusTransactionDetailId(Long.parseLong(transactionId));
    }
}
