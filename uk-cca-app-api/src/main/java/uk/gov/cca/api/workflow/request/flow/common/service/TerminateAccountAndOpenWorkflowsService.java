package uk.gov.cca.api.workflow.request.flow.common.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class TerminateAccountAndOpenWorkflowsService {
	
	private final RequestService requestService;
    private final TargetUnitAccountUpdateService targetUnitAccountUpdateService;
    private final RequestQueryService requestQueryService;
    private final WorkflowService workflowService;
    private final FacilityDataUpdateService facilityDataUpdateService;
    private final UnderlyingAgreementService underlyingAgreementService;
    private static final String TERMINATE_REASON = "Workflow terminated by the system because the account was terminated";
    private static final Set<String> EXCLUDED_REQUEST_TYPES = Set.of(CcaRequestType.NON_COMPLIANCE, CcaRequestType.FACILITY_AUDIT);

    @Transactional
    public void terminateAccountAndOpenWorkflows(final Request request, LocalDateTime terminationDate, String terminatedBy) {
    	
        final Long accountId = request.getAccountId();

        // Update Target Unit Account Status
        targetUnitAccountUpdateService.handleTargetUnitAccountTerminated(accountId, terminationDate);

        // Terminate facility data
        facilityDataUpdateService.terminateActiveFacilities(accountId, terminationDate);

        // Update terminated date of the related UnA documents
        underlyingAgreementService.terminateActiveUnaDocuments(accountId, terminationDate);

        // Any open WF related to this Target Unit account is terminated.
        terminateWorkflowsByAccountId(accountId, request.getType().getCode(), terminatedBy);

    }

    private void terminateWorkflowsByAccountId(Long accountId, String requestType, String terminatedBy) {
    	// Find requests related to this account that are in progress and should be terminated
        List<Request> accountRequests = requestQueryService.findInProgressRequestsByAccount(accountId).stream()
        		.filter(req -> !EXCLUDED_REQUEST_TYPES.contains(req.getType().getCode()) 
        				&& !req.getType().getCode().equals(requestType))
        		.toList();

        accountRequests.stream()
                .forEach(ar -> {
                            // A dedicated timeline event is registered to the terminated workflow,
                            // that "explains" the reason why the Workflow was terminated.
                            ar.setStatus(CcaRequestStatuses.CANCELLED);
                            requestService.addActionToRequest(ar,
                                    null,
                                    CcaRequestActionType.REQUEST_TERMINATED,
                                    terminatedBy);
                            workflowService.deleteProcessInstance(ar.getProcessInstanceId(), TERMINATE_REASON);
                        }
                );
    }
}
