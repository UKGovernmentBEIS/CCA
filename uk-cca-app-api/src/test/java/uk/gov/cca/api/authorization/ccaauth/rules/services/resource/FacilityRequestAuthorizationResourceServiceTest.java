package uk.gov.cca.api.authorization.ccaauth.rules.services.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
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
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.repository.AuthorizationRuleRepository;
import uk.gov.netz.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.RoleTypeAuthorizationServiceDelegator;
import uk.gov.netz.api.common.constants.RoleTypeConstants;

@ExtendWith(MockitoExtension.class)
class FacilityRequestAuthorizationResourceServiceTest {

	@InjectMocks
    private FacilityRequestAuthorizationResourceService service;
    
    @Mock
    private AuthorizationRuleRepository authorizationRuleRepository;
    
    @Mock
    private RoleTypeAuthorizationServiceDelegator roleTypeAuthorizationServiceDelegator;

    @Mock
    private AuthorizationRulesQueryService authorizationRulesQueryService;
    
    @Test
    void findRequestCreateActionsBySectorAssociationId() {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();
        final Long facilityId = 1L;

        final Set<String> userAllowedRequestTypes = Set.of("requestType");
        final List<AuthorizationRuleScopePermission> rules = List.of(
                AuthorizationRuleScopePermission.builder().resourceSubType("requestType").handler("handler").permission(null).build());
        final AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .permission(null)
                .build();

        when(authorizationRulesQueryService.findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST, RoleTypeConstants.OPERATOR))
                .thenReturn(userAllowedRequestTypes);
        when(authorizationRuleRepository.findRulePermissionsByResourceTypeScopeAndRoleType(
        		CcaResourceType.FACILITY, Scope.REQUEST_CREATE, RoleTypeConstants.OPERATOR)).thenReturn(rules);
        when(roleTypeAuthorizationServiceDelegator.isAuthorized(user, authorizationCriteria))
                .thenReturn(true);

        // Invoke
        Set<String> results = service.findRequestCreateActionsByFacilityId(user, facilityId);

        // Verify
        assertThat(results)
            .hasSize(1)
            .containsOnly("requestType");
        verify(authorizationRulesQueryService, times(1))
                .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST, RoleTypeConstants.OPERATOR);
        verify(authorizationRuleRepository, times(1))
                .findRulePermissionsByResourceTypeScopeAndRoleType(CcaResourceType.FACILITY, Scope.REQUEST_CREATE, RoleTypeConstants.OPERATOR);
        verify(roleTypeAuthorizationServiceDelegator, times(1))
                .isAuthorized(user, authorizationCriteria);
        verifyNoMoreInteractions(roleTypeAuthorizationServiceDelegator);
    }
}
