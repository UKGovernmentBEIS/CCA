package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.account.service.AccountCaSiteContactService;
import uk.gov.cca.api.authorization.core.service.UserRoleTypeService;
import uk.gov.cca.api.workflow.request.core.assignment.requestassign.RequestReleaseService;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.UserRoleRequestTaskDefaultAssignmentService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.cca.api.workflow.request.core.domain.RequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;

@Log4j2
@Service
@RequiredArgsConstructor
public class RegulatorRequestTaskDefaultAssignmentService implements UserRoleRequestTaskDefaultAssignmentService {

    private final RequestTaskAssignmentService requestTaskAssignmentService;
    private final AccountCaSiteContactService accountCaSiteContactService;
    private final UserRoleTypeService userRoleTypeService;
    private final RequestReleaseService requestReleaseService;

    @Override
    public RoleType getRoleType() {
        return RoleType.REGULATOR;
    }

    @Transactional
    public void assignDefaultAssigneeToTask(RequestTask requestTask) {
        boolean isSupportingTask = RequestTaskType.getSupportingRequestTaskTypes().contains(requestTask.getType());
        RequestPayload requestPayload = requestTask.getRequest().getPayload();
        String candidateAssignee = isSupportingTask ? requestPayload.getSupportingRegulator() : requestPayload.getRegulatorAssignee();

        if (!ObjectUtils.isEmpty(candidateAssignee) && userRoleTypeService.isUserRegulator(candidateAssignee)) {
            try {
                requestTaskAssignmentService.assignToUser(requestTask, candidateAssignee);
            } catch (BusinessCheckedException e) {
                assignTaskToCASiteContactOrReleaseRequest(requestTask, isSupportingTask);
            }
        } else {
            assignTaskToCASiteContactOrReleaseRequest(requestTask, isSupportingTask);
        }
    }

    private void assignTaskToCASiteContactOrReleaseRequest(RequestTask requestTask, boolean isSupportingTask) {
        accountCaSiteContactService
            .findCASiteContactByAccount(requestTask.getRequest().getAccountId())
            .ifPresentOrElse(
                caSiteContactUser -> {
                    try {
                        if (isSupportingTask) {
                            String firstReviewer = requestTask.getRequest().getPayload().getRegulatorReviewer();
                            if (!caSiteContactUser.equals(firstReviewer)) {
                                requestTaskAssignmentService.assignToUser(requestTask, caSiteContactUser);
                            }
                        } else {
                            requestTaskAssignmentService.assignToUser(requestTask, caSiteContactUser);
                        }
                    } catch (BusinessCheckedException e) {
                        log.error("Request task '{}' for regulator user will remain unassigned. Error msg : '{}'",
                            requestTask::getId, e::getMessage);
                        requestReleaseService.releaseRequest(requestTask);
                    }
                },
                () -> requestReleaseService.releaseRequest(requestTask)
            );
    }
}
