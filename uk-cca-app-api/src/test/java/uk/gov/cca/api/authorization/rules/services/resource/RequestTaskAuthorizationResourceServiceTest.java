package uk.gov.cca.api.authorization.rules.services.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;
import uk.gov.cca.api.authorization.rules.domain.Scope;
import uk.gov.cca.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.cca.api.authorization.rules.services.resource.OperatorAuthorityResourceService;
import uk.gov.cca.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.cca.api.authorization.rules.services.resource.RequestTaskAuthorizationResourceService;
import uk.gov.cca.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.cca.api.authorization.rules.services.resource.VerifierAuthorityResourceService;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestTaskAuthorizationResourceServiceTest {

    @InjectMocks
    private RequestTaskAuthorizationResourceService service;

    @Mock
    private OperatorAuthorityResourceService operatorAuthorityResourceService;

    @Mock
    private RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    @Mock
    private VerifierAuthorityResourceService verifierAuthorityResourceService;

    @Mock
    private ResourceScopePermissionService resourceScopePermissionService;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Test
    void hasUserExecuteScopeOnRequestTaskType() {
        RoleType roleType = RoleType.OPERATOR;
        AppUser authUser = AppUser.builder().roleType(roleType).build();
        String requestTaskType = "requestTaskType";
        Long accountId = 1L;
        Long verificationBodyId = 2L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String scope = Scope.REQUEST_TASK_EXECUTE;

        ResourceScopePermission resourceScopePermission =
            ResourceScopePermission.builder().permission(Permission.PERM_CA_USERS_EDIT).build();

        when(
            resourceScopePermissionService.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));

        ResourceCriteria resourceCriteria =
            ResourceCriteria.builder()
                    .accountId(accountId)
                    .competentAuthority(competentAuthority)
                    .verificationBodyId(verificationBodyId)
                    .build();
        boolean result = service.hasUserExecuteScopeOnRequestTaskType(authUser, requestTaskType, resourceCriteria);

        assertThat(result).isTrue();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(appAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(criteriaCaptured.getVerificationBodyId()).isEqualTo(verificationBodyId);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }


    @Test
    void hasUserExecuteScopeOnRequestTaskType_verifier() {
        RoleType roleType = RoleType.VERIFIER;
        AppUser authUser = AppUser.builder().roleType(roleType).build();
        String requestTaskType = "requestTaskType";
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String scope = Scope.REQUEST_TASK_EXECUTE;

        ResourceScopePermission resourceScopePermission =
                ResourceScopePermission.builder().permission(Permission.PERM_CA_USERS_EDIT).build();

        when(
                resourceScopePermissionService.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
                .thenReturn(Optional.of(resourceScopePermission));

        ResourceCriteria resourceCriteria =
                ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        boolean result = service.hasUserExecuteScopeOnRequestTaskType(authUser, requestTaskType, resourceCriteria);

        assertThat(result).isTrue();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
                ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(appAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getCompetentAuthority()).isNull();
        assertThat(criteriaCaptured.getVerificationBodyId()).isNull();
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }

    @Test
    void hasUserExecuteScopeOnRequestTaskType_not_authorized() {
        RoleType roleType = RoleType.OPERATOR;
        AppUser authUser = AppUser.builder().roleType(roleType).build();
        String requestTaskType = "requestTaskType";
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String scope = Scope.REQUEST_TASK_EXECUTE;

        ResourceScopePermission resourceScopePermission =
            ResourceScopePermission.builder().permission(Permission.PERM_CA_USERS_EDIT).build();

        AuthorizationCriteria authCriteria =
            AuthorizationCriteria.builder()
                .accountId(accountId)
                .competentAuthority(competentAuthority)
                .permission(Permission.PERM_CA_USERS_EDIT).build();

        when(
            resourceScopePermissionService.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));

        doThrow(BusinessException.class).when(appAuthorizationService).authorize(authUser, authCriteria);

        ResourceCriteria resourceCriteria =
            ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        boolean result = service.hasUserExecuteScopeOnRequestTaskType(authUser, requestTaskType, resourceCriteria);

        assertThat(result).isFalse();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(appAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }

    @Test
    void hasUserAssignScopeOnRequestTasks() {
        RoleType roleType = RoleType.OPERATOR;
        AppUser authUser = AppUser.builder().roleType(roleType).build();
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String scope = Scope.REQUEST_TASK_ASSIGN;

        ResourceScopePermission resourceScopePermission =
            ResourceScopePermission.builder().permission(Permission.PERM_TASK_ASSIGNMENT).build();

        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));

        ResourceCriteria resourceCriteria =
            ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        boolean result = service.hasUserAssignScopeOnRequestTasks(authUser, resourceCriteria);

        assertThat(result).isTrue();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, roleType, scope);
        verify(appAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }

    @Test
    void hasUserAssignScopeOnRequestTasks_not_authorized() {
        RoleType roleType = RoleType.OPERATOR;
        AppUser authUser = AppUser.builder().roleType(roleType).build();
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String scope = Scope.REQUEST_TASK_ASSIGN;

        ResourceScopePermission resourceScopePermission =
            ResourceScopePermission.builder().permission(Permission.PERM_TASK_ASSIGNMENT).build();

        AuthorizationCriteria authCriteria =
            AuthorizationCriteria.builder()
                .accountId(accountId)
                .competentAuthority(competentAuthority)
                .permission(Permission.PERM_TASK_ASSIGNMENT).build();

        when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, roleType, scope))
            .thenReturn(Optional.of(resourceScopePermission));

        doThrow(BusinessException.class).when(appAuthorizationService).authorize(authUser, authCriteria);

        ResourceCriteria resourceCriteria =
            ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        boolean result = service.hasUserAssignScopeOnRequestTasks(authUser, resourceCriteria);

        assertThat(result).isFalse();
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(resourceScopePermissionService, times(1)).findByResourceTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, roleType, scope);
        verify(appAuthorizationService, times(1)).authorize(Mockito.eq(authUser), criteriaCaptor.capture());
        AuthorizationCriteria criteriaCaptured = criteriaCaptor.getValue();
        assertThat(criteriaCaptured.getAccountId()).isEqualTo(accountId);
        assertThat(criteriaCaptured.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(criteriaCaptured.getPermission()).isEqualTo(resourceScopePermission.getPermission());
    }

    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_operator_requires_permission() {
        String requestTaskType = "requestTaskType";
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RoleType roleType = RoleType.OPERATOR;
        String scope = Scope.REQUEST_TASK_EXECUTE;

        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(true);

        List<String> users = List.of("user");
        when(operatorAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndAccountId(
            ResourceType.REQUEST_TASK, requestTaskType, scope, accountId))
            .thenReturn(users);

        ResourceCriteria resourceCriteria =
            ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        List<String> usersFound =
            service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);

        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(operatorAuthorityResourceService).findUsersWithScopeOnResourceTypeAndSubTypeAndAccountId(
            ResourceType.REQUEST_TASK, requestTaskType, scope, accountId);
    }

    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_operator_requires_no_permission() {
        String requestTaskType = "requestTaskType";
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RoleType roleType = RoleType.OPERATOR;
        String scope = Scope.REQUEST_TASK_EXECUTE;

        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(false);

        List<String> users = List.of("user");
        when(operatorAuthorityResourceService.findUsersByAccountId(accountId))
            .thenReturn(users);

        ResourceCriteria resourceCriteria =
            ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        List<String> usersFound =
            service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);

        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(operatorAuthorityResourceService).findUsersByAccountId(accountId);
    }

    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_regulator_requires_permission() {
        String requestTaskType = "requestTaskType";
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RoleType roleType = RoleType.REGULATOR;
        String scope = Scope.REQUEST_TASK_EXECUTE;

        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(true);

        List<String> users = List.of("user");
        when(regulatorAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
            ResourceType.REQUEST_TASK, requestTaskType, scope, competentAuthority))
            .thenReturn(users);

        ResourceCriteria resourceCriteria =
            ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        List<String> usersFound =
            service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);

        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(regulatorAuthorityResourceService).findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
            ResourceType.REQUEST_TASK, requestTaskType, scope, competentAuthority);
    }

    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_regulator_requires_no_permission() {
        String requestTaskType = "requestTaskType";
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RoleType roleType = RoleType.REGULATOR;
        String scope = Scope.REQUEST_TASK_EXECUTE;

        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(false);

        List<String> users = List.of("user");
        when(regulatorAuthorityResourceService.findUsersByCompetentAuthority(competentAuthority))
            .thenReturn(users);

        ResourceCriteria resourceCriteria =
            ResourceCriteria.builder().accountId(accountId).competentAuthority(competentAuthority).build();
        List<String> usersFound =
            service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);

        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(regulatorAuthorityResourceService).findUsersByCompetentAuthority(competentAuthority);
    }

    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_verifier_requires_permission() {
        String requestTaskType = "requestTaskType";
        Long verificationBodyId = 1L;
        RoleType roleType = RoleType.VERIFIER;
        String scope = Scope.REQUEST_TASK_EXECUTE;

        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(true);

        List<String> users = List.of("user");
        when(verifierAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndVerificationBodyId(
            ResourceType.REQUEST_TASK, requestTaskType, scope, verificationBodyId))
            .thenReturn(users);

        ResourceCriteria resourceCriteria =
            ResourceCriteria.builder().verificationBodyId(verificationBodyId).build();
        List<String> usersFound =
            service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);

        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(verifierAuthorityResourceService).findUsersWithScopeOnResourceTypeAndSubTypeAndVerificationBodyId(
            ResourceType.REQUEST_TASK, requestTaskType, scope, verificationBodyId);
    }

    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType_verifier_requires_no_permission() {
        String requestTaskType = "requestTaskType";
        Long verificationBodyId = 1L;
        RoleType roleType = RoleType.VERIFIER;
        String scope = Scope.REQUEST_TASK_EXECUTE;

        when(resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope))
            .thenReturn(false);

        List<String> users = List.of("user");
        when(verifierAuthorityResourceService.findUsersByVerificationBodyId(verificationBodyId))
            .thenReturn(users);

        ResourceCriteria resourceCriteria =
            ResourceCriteria.builder().verificationBodyId(verificationBodyId).build();
        List<String> usersFound =
            service.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType, resourceCriteria, roleType);

        assertThat(usersFound).containsAll(users);
        verify(resourceScopePermissionService, times(1)).existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType.REQUEST_TASK, requestTaskType, roleType, scope);
        verify(verifierAuthorityResourceService).findUsersByVerificationBodyId(verificationBodyId);
    }

    @Test
    void findRequestTaskTypesByRoleType() {
        RoleType roleType = RoleType.REGULATOR;

        when(resourceScopePermissionService.findByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, roleType))
            .thenReturn(
                Set.of(
                    ResourceScopePermission.builder().resourceSubType("requestTaskType").build(),
                    ResourceScopePermission.builder().resourceSubType(null).build()
                )
            );

        Set<String> actualRequestTaskTypes = service.findRequestTaskTypesByRoleType(roleType);

        assertThat(actualRequestTaskTypes).containsOnly("requestTaskType");
    }

}
