package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsistenceFeesMoaTargetUnitAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaTargetUnitAccessRuleHandlerTest {

	@InjectMocks
    private SubsistenceFeesMoaTargetUnitAccessRuleHandler handler;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Mock
    private SubsistenceFeesMoaTargetUnitAuthorityInfoProvider authorityInfoProvider;

    @Test
    void evaluateRules() {
        String resourceId = "1000";
        Long accountId = 1L;
        AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
                .handler("subsistenceFeesMoaTargetUnitAccessHandler")
                .build();

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
                .permission(rule.getPermission())
                .build();

        when(authorityInfoProvider.getAccountIdByMoaTargetUnitId(Long.parseLong(resourceId))).thenReturn(accountId);

        // invoke
        handler.evaluateRules(Set.of(rule), user, resourceId);

        // verify
        verify(appAuthorizationService, times(1)).authorize(user, authorizationCriteria);
        verify(authorityInfoProvider, times(1)).getAccountIdByMoaTargetUnitId(Long.parseLong(resourceId));
    }
}
