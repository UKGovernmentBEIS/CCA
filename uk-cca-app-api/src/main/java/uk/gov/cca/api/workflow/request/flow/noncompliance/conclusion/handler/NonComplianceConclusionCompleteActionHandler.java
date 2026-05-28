package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceOutcome;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonCompliancePenaltyOutcomeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.service.NonComplianceConclusionSubmitService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.validation.NonComplianceConclusionCompleteValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NonComplianceConclusionCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final NonComplianceConclusionSubmitService nonComplianceConclusionSubmitService;
    private final NonComplianceConclusionCompleteValidator validator;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload =
                (NonComplianceConclusionSubmitRequestTaskPayload) requestTask.getPayload();
        final boolean isReissuePenaltyNeeded =
                requestTaskPayload.getNonComplianceConclusion().getDetails().getPenaltyOutcome().equals(NonCompliancePenaltyOutcomeType.REISSUE);

        // Validate
        validator.validate(requestTaskPayload);

        // Update Request
        nonComplianceConclusionSubmitService.complete(requestTask, isReissuePenaltyNeeded);

        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.SUBMITTED,
                        CcaBpmnProcessConstants.IS_NON_COMPLIANCE_REISSUE_PENALTY_NEEDED, isReissuePenaltyNeeded));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_CONCLUSION_COMPLETE_APPLICATION);
    }
}
