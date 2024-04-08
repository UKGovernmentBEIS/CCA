package uk.gov.cca.api.authorization.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.cca.api.authorization.rules.services.handlers.AccountRequestCreateRuleHandler;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AccountRequestCreateRuleHandlerTest {

    @InjectMocks
    private AccountRequestCreateRuleHandler handler;

    @Mock
    private AppAuthorizationService appAuthorizationService;
    
    private final AppUser USER = AppUser.builder().roleType(RoleType.OPERATOR).build();
    
    @Test
    void evaluateRules_empty_rules() {
        String resourceId = "1";
        
        BusinessException be = assertThrows(BusinessException.class, () -> handler.evaluateRules(Set.of(), USER,
                resourceId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
        verifyNoInteractions(appAuthorizationService);
    }
    
    @Test
    void evaluateRules() {
        String resourceId = "1";
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
            .resourceSubType("requestCreateActionType")
            .build();
        
        handler.evaluateRules(Set.of(authorizationRulePermissionScope1), USER,
                resourceId);

        verify(appAuthorizationService, times(1)).authorize(USER, AuthorizationCriteria.builder().accountId(Long.valueOf(resourceId)).permission(Permission.PERM_ACCOUNT_USERS_EDIT).build());
        verifyNoMoreInteractions(appAuthorizationService);
    }
    
    @Test
    void evaluateRules_no_account_resource_id() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope2 = 
                AuthorizationRuleScopePermission.builder()
            .resourceSubType("requestCreateActionType")
            .build();
        
        handler.evaluateRules(Set.of(authorizationRulePermissionScope2), USER, null);

        verifyNoInteractions(appAuthorizationService);
    }
}
