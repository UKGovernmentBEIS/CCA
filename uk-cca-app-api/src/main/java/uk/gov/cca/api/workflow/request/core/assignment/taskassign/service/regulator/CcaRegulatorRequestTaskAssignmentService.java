package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskReleaseService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class CcaRegulatorRequestTaskAssignmentService {

    private final RequestTaskRepository requestTaskRepository;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final RequestTaskAssignmentService requestTaskAssignmentService;
    private final RequestTaskReleaseService requestTaskReleaseService;

    public void assignTasksToSiteContactOrRelease(String userId) {
        List<RequestTask> requestTasks = requestTaskRepository.findByAssignee(userId);
        if (!requestTasks.isEmpty()) {
            doAssignTasksToSiteContactOrRelease(requestTasks);
        }
    }

    private void doAssignTasksToSiteContactOrRelease(List<RequestTask> requestTasks) {
        requestTasks.forEach(rt -> {
            Long sectorAssociationId = targetUnitAccountQueryService.getAccountById(rt.getRequest().getAccountId()).getSectorAssociationId();
            String facilitatorUserId = sectorAssociationQueryService.getSectorAssociationFacilitatorUserId(sectorAssociationId);
            assignTaskToSiteContactOrRelease(rt, facilitatorUserId);
        });
    }

    private void assignTaskToSiteContactOrRelease(RequestTask requestTask, String facilitatorUserId) {
        if (!ObjectUtils.isEmpty(facilitatorUserId)) {
            try {
                requestTaskAssignmentService.assignToUser(requestTask, facilitatorUserId);
            } catch (BusinessCheckedException ex) {
                releaseTask(requestTask);
            }
        } else {
            releaseTask(requestTask);
        }
    }

    private void releaseTask(RequestTask requestTask) {
        try {
            requestTaskReleaseService.releaseTaskForced(requestTask);
        } catch (BusinessException ex) {
            log.error("Cannot release task '{}'. Error message: '{}'", requestTask::getId, ex::getMessage);
        }
    }
}
