package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.service.UserRoleTypeService;
import uk.gov.cca.api.authorization.rules.services.resource.RequestTaskAuthorizationResourceService;
import uk.gov.cca.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;

import java.util.List;

import static uk.gov.netz.api.common.domain.RoleType.OPERATOR;

/**
 * Validation Service for assignments.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class RequestTaskAssignmentValidationService {

    private final UserRoleTypeService userRoleTypeService;
    private final RequestTaskAuthorizationResourceService requestTaskAuthorizationResourceService;

    /**
     * Checks if the {@code requestTask} can be released, taking into consideration the role type of the users
     * to whom the task is addressed to.
     * Only request tasks addressed to {@link RoleType#OPERATOR} can not be released.
     * @param requestTask the {@link RequestTask}
     */
    void validateTaskReleaseCapability(RequestTask requestTask, RoleType roleType) {
        validateTaskAssignmentCapability(requestTask);

        if (roleType == null || OPERATOR.equals(roleType)) {
            log.warn("Task to be handled by '{}' can not be released", () -> roleType);
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED);
        }
    }

    /**
     * Checks if the {@code requestTask} can be assigned to user.
     * @param requestTask the {@link RequestTask}
     */
    void validateTaskAssignmentCapability(RequestTask requestTask) {
        validateTaskAssignmentCapability(requestTask.getType());
    }

    void validateTaskAssignmentCapability(RequestTaskType requestTaskType) {
        if (!requestTaskType.isAssignable()) {
            throw new BusinessException(ErrorCode.REQUEST_TASK_NOT_ASSIGNABLE);
        }
    }

    public boolean hasUserPermissionsToBeAssignedToTask(RequestTask requestTask, String userId) {
        RoleType userRoleType = userRoleTypeService.getUserRoleTypeByUserId(userId).getRoleType();
        
        ResourceCriteria resourceCriteria = 
                ResourceCriteria.builder()
                    .accountId(requestTask.getRequest().getAccountId())
                    .competentAuthority(requestTask.getRequest().getCompetentAuthority())
                    .verificationBodyId(requestTask.getRequest().getVerificationBodyId())
                    .build();
        
        List<String> candidateAssignees = getCandidateAssigneesByCriteriaAndRoleType(
            requestTask.getType(),
            resourceCriteria,
            userRoleType);

        return candidateAssignees.contains(userId);
    }

    public boolean hasUserPermissionsToBeAssignedToTaskType(RequestTask requestTask, RequestTaskType requestTaskType,
                                                            String userId, AppUser appUser) {
        Request request = requestTask.getRequest();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
            .accountId(request.getAccountId())
            .competentAuthority(request.getCompetentAuthority())
            .build();

        List<String> candidateAssignees = getCandidateAssigneesByCriteriaAndRoleType(
            requestTaskType,
            resourceCriteria,
            appUser.getRoleType());

        if (RequestTaskType.getSupportingRequestTaskTypes().contains(requestTaskType)) {
            candidateAssignees.remove(appUser.getUserId());
        }

        return candidateAssignees.contains(userId);
    }

    private List<String> getCandidateAssigneesByCriteriaAndRoleType(RequestTaskType requestTaskType,
                                                                    ResourceCriteria resourceCriteria, RoleType roleType) {
        return requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
            requestTaskType.name(),
            resourceCriteria,
            roleType);
    }
}
