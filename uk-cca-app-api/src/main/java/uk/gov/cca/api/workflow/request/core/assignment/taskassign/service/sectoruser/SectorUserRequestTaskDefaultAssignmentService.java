package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.sectoruser;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaUserRoleTypeService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskReleaseService;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.UserRoleRequestTaskDefaultAssignmentService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Log4j2
@Service
@RequiredArgsConstructor
public class SectorUserRequestTaskDefaultAssignmentService implements UserRoleRequestTaskDefaultAssignmentService {

    private final RequestTaskAssignmentService requestTaskAssignmentService;
    private final CcaUserRoleTypeService ccaUserRoleTypeService;
    private final TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;
    private final RequestTaskReleaseService requestTaskReleaseService;

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }

    @Transactional
    public void assignDefaultAssigneeToTask(RequestTask requestTask) {
        CcaRequestPayload requestPayload = (CcaRequestPayload) requestTask.getRequest().getPayload();
        String candidateAssignee = requestPayload.getSectorUserAssignee();

        if(!ObjectUtils.isEmpty(candidateAssignee) && ccaUserRoleTypeService.isUserSectorUser(candidateAssignee)) {
            try {
                requestTaskAssignmentService.assignToUser(requestTask, candidateAssignee);
            } catch (BusinessCheckedException e) {
                assignTaskToSiteContactOrReleaseRequest(requestTask);
            }
        } else {
            assignTaskToSiteContactOrReleaseRequest(requestTask);
        }
    }

    public void assignTaskToSiteContactOrReleaseRequest(RequestTask requestTask) {
        targetUnitAccountSiteContactService
            .findTargetUnitAccountSiteContactByAccountId(requestTask.getRequest().getAccountId())
            .ifPresentOrElse(
                siteContactUser -> {
                    try {
                        requestTaskAssignmentService.assignToUser(requestTask, siteContactUser);
                    } catch (BusinessCheckedException e) {
                        log.error("Request task '{}' for sector user will remain unassigned. Error msg : '{}'" ,
                                requestTask::getId, e::getMessage);
                        requestTaskReleaseService.releaseTaskForced(requestTask);
                    }
                },
                () -> requestTaskReleaseService.releaseTaskForced(requestTask)
            );
    }
}
