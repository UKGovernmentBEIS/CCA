package uk.gov.cca.api.authorization.ccaauth.rules.services.resource;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.repository.AuthorizationRuleRepository;
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
    
    @Test
    void findRequestCreateActionsBySectorAssociationId() {
        AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();
        Long facilityId = 1L;
        
        List<AuthorizationRuleScopePermission> rules = List.of(
                AuthorizationRuleScopePermission.builder().resourceSubType("requestType").handler("handler").permission(null).build());
        
        when(authorizationRuleRepository.findRulePermissionsByResourceTypeScopeAndRoleType(
        		CcaResourceType.FACILITY, Scope.REQUEST_CREATE, RoleTypeConstants.OPERATOR)).thenReturn(rules);
        
        when(roleTypeAuthorizationServiceDelegator.isAuthorized(user, AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
        		.permission(null).build()))
        .thenReturn(true);
        
        Set<String> results = service.findRequestCreateActionsByFacilityId(user, facilityId);
        
        assertThat(results)
            .hasSize(1)
            .containsOnly("requestType");
    }
}
