package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTerminationFinalisedService {
    private final RequestService requestService;
    private final TargetUnitAccountUpdateService targetUnitAccountUpdateService;
    private final RequestQueryService requestQueryService;
    private final WorkflowService workflowService;
    private final FacilityDataUpdateService facilityDataUpdateService;
    private static final String TERMINATE_REASON = "Workflow terminated by the system because the final decision is \"Terminate agreement\"";

    @Transactional
    public void terminateAccountAndOpenWorkflows(String requestId){
        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();
        final LocalDateTime terminationDate = LocalDateTime.now();

        // Update Target Unit Account Status
        targetUnitAccountUpdateService.handleTargetUnitAccountTerminated(accountId, terminationDate);

        // Terminate facility data
        facilityDataUpdateService.terminateFacilities(accountId, terminationDate);

        // Any open WF related to this Target Unit account is terminated.
        terminateWorkflowsByAccountId(accountId);

    }

    private void terminateWorkflowsByAccountId(Long accountId) {
        List<Request> accountRequests = requestQueryService.findInProgressRequestsByAccount(accountId);

        accountRequests.stream()
                .filter(ar -> !ar.getType().getCode().equals(CcaRequestType.ADMIN_TERMINATION))
                .forEach(ar -> {
                    // A dedicated timeline event is registered to the terminated workflow,
                    // that "explains" the reason why the Workflow was terminated.
                    ar.setStatus(CcaRequestStatuses.CANCELLED);
                    requestService.addActionToRequest(ar,
                            null,
                            CcaRequestActionType.REQUEST_TERMINATED,
                            ar.getPayload().getRegulatorAssignee());
                    workflowService.deleteProcessInstance(ar.getProcessInstanceId(), TERMINATE_REASON);
                    }
                );
    }

}
