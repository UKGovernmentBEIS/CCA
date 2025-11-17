package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

@ExtendWith(MockitoExtension.class)
class FacilityRequestCreateRuleHandlerTest {

	@InjectMocks
    private FacilityRequestCreateRuleHandler facilityRequestCreateRuleHandler;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Test
    void evaluateRules() {
        Long facilityId = 1L;
        AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
                .handler("facilityRequestCreateHandler")
                .permission("permission1")
                .build();

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .permission(rule.getPermission())
                .build();

        facilityRequestCreateRuleHandler.evaluateRules(Set.of(rule), user, String.valueOf(facilityId));

        verify(appAuthorizationService, times(1)).authorize(user, authorizationCriteria);
    }
}
