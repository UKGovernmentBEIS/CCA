package uk.gov.cca.api.authorization.rules.services.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;
import uk.gov.cca.api.authorization.rules.domain.Scope;
import uk.gov.cca.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestTaskAuthorizationResourceService {

    private final OperatorAuthorityResourceService operatorAuthorityResourceService;
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;
    private final VerifierAuthorityResourceService verifierAuthorityResourceService;
    private final ResourceScopePermissionService resourceScopePermissionService;
    private final AppAuthorizationService appAuthorizationService;

    public boolean hasUserExecuteScopeOnRequestTaskType(AppUser authUser, String requestTaskType, ResourceCriteria resourceCriteria) {
        String requiredPermission =
                resourceScopePermissionService.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, requestTaskType, authUser.getRoleType(), Scope.REQUEST_TASK_EXECUTE)
                        .map(ResourceScopePermission::getPermission)
                        .orElse(null);

        AuthorizationCriteria authCriteria = getAuthorizationCriteriaByRoleType(authUser.getRoleType(), resourceCriteria, requiredPermission);
        try {
            appAuthorizationService.authorize(authUser, authCriteria);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    public boolean hasUserAssignScopeOnRequestTasks(AppUser authUser, ResourceCriteria resourceCriteria) {
        String requiredPermission =
                resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, authUser.getRoleType(), Scope.REQUEST_TASK_ASSIGN)
                        .map(ResourceScopePermission::getPermission)
                        .orElse(null);

        AuthorizationCriteria authCriteria = getAuthorizationCriteriaByRoleType(authUser.getRoleType(), resourceCriteria, requiredPermission);

        try {
            appAuthorizationService.authorize(authUser, authCriteria);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    public List<String> findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
            String requestTaskType, ResourceCriteria resourceCriteria, RoleType roleType) {
        boolean requiresPermission =
                resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, requestTaskType, roleType, Scope.REQUEST_TASK_EXECUTE);

        switch (roleType) {
            case OPERATOR:
                return findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaOperator(requestTaskType, resourceCriteria, requiresPermission);
            case REGULATOR:
                return findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaRegulator(requestTaskType, resourceCriteria, requiresPermission);
            case VERIFIER:
                return findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaVerifier(requestTaskType, resourceCriteria, requiresPermission);
            default:
                throw new UnsupportedOperationException(String.format("Role type %s is not supported yet", roleType));
        }

    }

    public Set<String> findRequestTaskTypesByRoleType(RoleType roleType) {
        return resourceScopePermissionService.findByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, roleType).stream()
                .map(ResourceScopePermission::getResourceSubType)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private AuthorizationCriteria getAuthorizationCriteriaByRoleType(RoleType roleType, ResourceCriteria resourceCriteria, String requiredPermission) {
        return roleType == RoleType.VERIFIER ?
                AuthorizationCriteria.builder()
                        .accountId(resourceCriteria.getAccountId())
                        .permission(requiredPermission).build() :
                AuthorizationCriteria.builder()
                        .accountId(resourceCriteria.getAccountId())
                        .competentAuthority(resourceCriteria.getCompetentAuthority())
                        .verificationBodyId(resourceCriteria.getVerificationBodyId())
                        .permission(requiredPermission).build();
    }

    private List<String> findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaVerifier(String requestTaskType, ResourceCriteria resourceCriteria, boolean requiresPermission) {
        if (!requiresPermission) {
            return verifierAuthorityResourceService.findUsersByVerificationBodyId(resourceCriteria.getVerificationBodyId());
        } else {
            return verifierAuthorityResourceService.
                    findUsersWithScopeOnResourceTypeAndSubTypeAndVerificationBodyId(
                    ResourceType.REQUEST_TASK, requestTaskType, Scope.REQUEST_TASK_EXECUTE, resourceCriteria.getVerificationBodyId());
        }
    }

    private List<String> findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaRegulator(String requestTaskType, ResourceCriteria resourceCriteria, boolean requiresPermission) {
        if (!requiresPermission) {
            return regulatorAuthorityResourceService.findUsersByCompetentAuthority(resourceCriteria.getCompetentAuthority());
        } else {
            return regulatorAuthorityResourceService.
                    findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
                    ResourceType.REQUEST_TASK, requestTaskType, Scope.REQUEST_TASK_EXECUTE, resourceCriteria.getCompetentAuthority());
        }
    }

    private List<String> findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaOperator(String requestTaskType,
                                                                                        ResourceCriteria resourceCriteria,
                                                                                        boolean requiresPermission) {
        if (!requiresPermission) {
            return operatorAuthorityResourceService.findUsersByAccountId(resourceCriteria.getAccountId());
        } else {
            return operatorAuthorityResourceService.
                    findUsersWithScopeOnResourceTypeAndSubTypeAndAccountId(
                    ResourceType.REQUEST_TASK, requestTaskType, Scope.REQUEST_TASK_EXECUTE, resourceCriteria.getAccountId());
        }
    }
}
