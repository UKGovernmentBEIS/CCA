package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class FacilityRequestCreateRuleHandlerTest {

	@InjectMocks
    private FacilityRequestCreateRuleHandler facilityRequestCreateRuleHandler;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Mock
    private AuthorizationRulesQueryService authorizationRulesQueryService;

    @Test
    void evaluateRules() {
        final Long facilityId = 1L;
        final AppUser user = AppUser.builder().roleType(CcaRoleTypeConstants.SECTOR_USER).build();
        final AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
                .resourceSubType("requestType")
                .handler("facilityRequestCreateHandler")
                .permission("permission1")
                .build();

        final Set<String> userAllowedRequestTypes = Set.of("requestType", "requestType2");
        final AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .permission(rule.getPermission())
                .build();

        when(authorizationRulesQueryService.findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST, CcaRoleTypeConstants.SECTOR_USER))
                .thenReturn(userAllowedRequestTypes);

        // Invoke
        facilityRequestCreateRuleHandler.evaluateRules(Set.of(rule), user, String.valueOf(facilityId));

        // Verify
        verify(authorizationRulesQueryService, times(1))
                .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST, CcaRoleTypeConstants.SECTOR_USER);
        verify(appAuthorizationService, times(1)).authorize(user, authorizationCriteria);
    }

    @Test
    void evaluateRules_no_allowed_request() {
        final Long facilityId = 1L;
        final AppUser user = AppUser.builder().roleType(CcaRoleTypeConstants.SECTOR_USER).build();
        final AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
                .resourceSubType("requestType")
                .handler("facilityRequestCreateHandler")
                .permission("permission1")
                .build();

        final Set<String> userAllowedRequestTypes = Set.of("requestType1", "requestType2");

        when(authorizationRulesQueryService.findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST, CcaRoleTypeConstants.SECTOR_USER))
                .thenReturn(userAllowedRequestTypes);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> facilityRequestCreateRuleHandler.evaluateRules(Set.of(rule), user, String.valueOf(facilityId)));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
        verify(authorizationRulesQueryService, times(1))
                .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST, CcaRoleTypeConstants.SECTOR_USER);
        verifyNoInteractions(appAuthorizationService);
    }

    @Test
    void evaluateRules_empty_rules() {
        final Long facilityId = 1L;
        final AppUser user = AppUser.builder().roleType(CcaRoleTypeConstants.SECTOR_USER).build();

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> facilityRequestCreateRuleHandler.evaluateRules(Set.of(), user, String.valueOf(facilityId)));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
        verifyNoInteractions(authorizationRulesQueryService, appAuthorizationService);
    }
}
