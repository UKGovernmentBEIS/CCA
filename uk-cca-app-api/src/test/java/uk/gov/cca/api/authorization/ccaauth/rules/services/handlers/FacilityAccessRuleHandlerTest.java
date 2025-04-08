package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.FacilityAuthorityInfoProvider;
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
class FacilityAccessRuleHandlerTest {

    @InjectMocks
    private FacilityAccessRuleHandler facilityAccessRuleHandler;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Mock
    private FacilityAuthorityInfoProvider facilityAuthorityInfoProvider;

    @Test
    void evaluateRules() {
        String facilityId = "ADS_1-F00023";
        long accountId = 1L;
        AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
                .handler("facilityAccessHandler")
                .build();

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .requestResources(Map.of(ResourceType.ACCOUNT, Long.toString(accountId)))
                .permission(rule.getPermission())
                .build();

        when(facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);

        // invoke
        facilityAccessRuleHandler.evaluateRules(Set.of(rule), user, facilityId);

        // verify
        verify(appAuthorizationService, times(1)).authorize(user, authorizationCriteria);
        verify(facilityAuthorityInfoProvider, times(1)).getAccountIdByFacilityId(facilityId);
    }
}
