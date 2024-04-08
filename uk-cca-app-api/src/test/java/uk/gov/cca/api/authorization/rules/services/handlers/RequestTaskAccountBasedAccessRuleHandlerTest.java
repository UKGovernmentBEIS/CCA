package uk.gov.cca.api.authorization.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.RequestTaskAuthorityInfoDTO;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.providers.RequestTaskAuthorityInfoProvider;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.cca.api.authorization.rules.services.handlers.RequestTaskAccountBasedAccessRuleHandler;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestTaskAccountBasedAccessRuleHandlerTest {
    @InjectMocks
    private RequestTaskAccountBasedAccessRuleHandler requestTaskAccessRuleHandler;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Mock
    private RequestTaskAuthorityInfoProvider requestTaskAuthorityInfoProvider;

    private final AppUser USER = AppUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();

    @Test
    void wrong_resourceId_type() {
        AuthorizationRuleScopePermission authorizationRule1 =
                AuthorizationRuleScopePermission.builder()
                        .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                        .build();

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);
        assertThrows(NumberFormatException.class,
                () -> requestTaskAccessRuleHandler.evaluateRules(rules, USER, "wrong"));

        verifyNoMoreInteractions(appAuthorizationService);
    }

    @Test
    void requestTask_does_not_exist() {
        AuthorizationRuleScopePermission authorizationRule1 =
                AuthorizationRuleScopePermission.builder()
                        .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                        .build();

        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(1L)).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> requestTaskAccessRuleHandler.evaluateRules(rules, USER, "1"));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        verifyNoMoreInteractions(appAuthorizationService);
    }

    @Test
    void single_rule() {
        AuthorizationRuleScopePermission authorizationRule1 =
                AuthorizationRuleScopePermission.builder()
                        .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                        .resourceSubType("ACCOUNT_USERS_SETUP")
                        .build();

        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = RequestTaskAuthorityInfoDTO.builder()
                .type("ACCOUNT_USERS_SETUP")
                .assignee(USER.getUserId())
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .accountId(2L)
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .verificationBodyId(1L).build())
                .build();
        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(1L)).thenReturn(requestTaskInfoDTO);

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);
        requestTaskAccessRuleHandler.evaluateRules(rules, USER, "1");

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
                .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                .build();
        verify(appAuthorizationService, times(1)).authorize(USER, authorizationCriteria);
        verifyNoMoreInteractions(appAuthorizationService);
    }

    @Test
    void single_rule_no_matching() {
        AuthorizationRuleScopePermission authorizationRule1 =
                AuthorizationRuleScopePermission.builder()
                        .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                        .resourceSubType("TYPE")
                        .build();

        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = RequestTaskAuthorityInfoDTO.builder()
                .type("ACCOUNT_USERS_SETUP")
                .assignee(USER.getUserId())
                .build();
        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(1L)).thenReturn(requestTaskInfoDTO);

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);

        //invoke
        BusinessException exception = assertThrows(BusinessException.class,
                () -> requestTaskAccessRuleHandler.evaluateRules(rules, USER, "1"));

        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());

        verifyNoMoreInteractions(appAuthorizationService);
    }

    @Test
    void multiple_rules() {
        AuthorizationRuleScopePermission authorizationRule1 =
                AuthorizationRuleScopePermission.builder()
                        .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                        .resourceSubType("ACCOUNT_USERS_SETUP")
                        .build();

        AuthorizationRuleScopePermission authorizationRule2 =
                AuthorizationRuleScopePermission.builder()
                        .permission(Permission.PERM_CA_USERS_EDIT)
                        .resourceSubType("ACCOUNT_USERS_SETUP")
                        .build();

        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = RequestTaskAuthorityInfoDTO.builder()
                .type("ACCOUNT_USERS_SETUP")
                .assignee(USER.getUserId())
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .accountId(2L)
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .verificationBodyId(2L).build())
                .build();
        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(1L)).thenReturn(requestTaskInfoDTO);

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1, authorizationRule2);
        requestTaskAccessRuleHandler.evaluateRules(rules, USER, "1");

        AuthorizationCriteria authorizationCriteria1 = AuthorizationCriteria.builder()
                .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
                .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                .build();
        verify(appAuthorizationService, times(1)).authorize(USER, authorizationCriteria1);

        AuthorizationCriteria authorizationCriteria2 = AuthorizationCriteria.builder()
                .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
                .permission(Permission.PERM_CA_USERS_EDIT)
                .build();
        verify(appAuthorizationService, times(1)).authorize(USER, authorizationCriteria2);
        verifyNoMoreInteractions(appAuthorizationService);
    }

    @Test
    void multiple_rules_one_applicable() {
        AuthorizationRuleScopePermission authorizationRule1 =
                AuthorizationRuleScopePermission.builder()
                        .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                        .resourceSubType("ACCOUNT_USERS_SETUP")
                        .build();

        AuthorizationRuleScopePermission authorizationRule2 =
                AuthorizationRuleScopePermission.builder()
                        .permission(Permission.PERM_CA_USERS_EDIT)
                        .resourceSubType("TYPE")
                        .build();

        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = RequestTaskAuthorityInfoDTO.builder()
                .type("ACCOUNT_USERS_SETUP")
                .assignee(USER.getUserId())
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .accountId(2L)
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .verificationBodyId(2L).build())
                .build();
        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(1L)).thenReturn(requestTaskInfoDTO);

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1, authorizationRule2);
        requestTaskAccessRuleHandler.evaluateRules(rules, USER, "1");

        AuthorizationCriteria authorizationCriteria1 = AuthorizationCriteria.builder()
                .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
                .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                .build();
        verify(appAuthorizationService, times(1)).authorize(USER, authorizationCriteria1);
        verifyNoMoreInteractions(appAuthorizationService);
    }
}