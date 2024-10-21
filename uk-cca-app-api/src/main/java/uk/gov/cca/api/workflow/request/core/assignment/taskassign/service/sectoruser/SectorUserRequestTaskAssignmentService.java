package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.sectoruser;

import lombok.AllArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.UserRoleRequestTaskAssignmentService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@AllArgsConstructor
public class SectorUserRequestTaskAssignmentService implements UserRoleRequestTaskAssignmentService {

    private final RequestTaskAssignmentService requestTaskAssignmentService;
    private final SectorUserRequestTaskDefaultAssignmentService sectorUserRequestTaskDefaultAssignmentService;
    private final RequestTaskRepository requestTaskRepository;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Transactional
    public void assignTask(RequestTask requestTask, String userId) {
        try {
            requestTaskAssignmentService.assignToUser(requestTask, userId);
        } catch (BusinessCheckedException e) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED);
        }
    }

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }

    public void assignTasksToSiteContactOrRelease(String userDeleted, Long sectorAssociationId) {
        List<RequestTask> requestTasks = this.requestTaskRepository.findByAssigneeAndRequestStatus(userDeleted, RequestStatuses.IN_PROGRESS);
        if (CollectionUtils.isNotEmpty(requestTasks)) {

            List<Long> accountsBySectorId = targetUnitAccountQueryService.getAllTargetUnitAccountIdsBySectorAssociationId(sectorAssociationId);

            List<RequestTask> filteredRequestTasks = requestTasks.stream().filter(requestTask -> accountsBySectorId.contains(requestTask.getRequest().getAccountId())).collect(Collectors.toList());

            filteredRequestTasks.forEach(sectorUserRequestTaskDefaultAssignmentService::assignTaskToSiteContactOrReleaseRequest);
        }
    }
}
