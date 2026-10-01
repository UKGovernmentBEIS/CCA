package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TerminateOpenFacilityWorkflowsService {

    private final RequestService requestService;
    private final RequestQueryService requestQueryService;
    private final WorkflowService workflowService;

    private static final String TERMINATE_REASON = "Workflow terminated by the system due to successful CSV update for the target period";

    @Transactional
    public void terminateOpenWorkflows(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PerformanceDataFacilityProcessingRequestPayload requestPayload =
                (PerformanceDataFacilityProcessingRequestPayload) request.getPayload();
        final String terminatedBy = requestPayload.getSectorUserAssignee();

        Long facilityId = requestPayload.getFacility().getId();
        TargetPeriodType targetPeriodType = requestPayload.getTargetPeriodType();

        // Any open TPR Digital Form WF related to this facility with the same target period is terminated.
        terminateWorkflowByFacilityIdAndTargetPeriod(facilityId, targetPeriodType, terminatedBy);
    }

    private void terminateWorkflowByFacilityIdAndTargetPeriod(Long facilityId, TargetPeriodType targetPeriodType, String terminatedBy) {
        // Find digital form request of this facility that is in progress and has the same target period
        Optional<Request> facilityRequest = requestQueryService.findInProgressRequestsByResource(facilityId, CcaResourceType.FACILITY).stream()
                .filter(req -> req.getType().getCode().equals(CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM))
                .filter(req -> {
                    PerformanceDataFacilityRequestMetadata metadata = (PerformanceDataFacilityRequestMetadata) req.getMetadata();
                    return metadata.getTargetPeriodType().equals(targetPeriodType);
                })
                .findFirst();

        facilityRequest.ifPresent(req -> {
            // A dedicated timeline event is registered to the terminated workflow,
            // that "explains" the reason why the Workflow was terminated.
            req.setStatus(CcaRequestStatuses.CANCELLED);
            requestService.addActionToRequest(req,
                    null,
                    CcaRequestActionType.REQUEST_TERMINATED_DUE_TO_PERFORMANCE_DATA_CSV_UPDATE,
                    terminatedBy);
            workflowService.deleteProcessInstance(req.getProcessInstanceId(), TERMINATE_REASON);
        });
    }
}
