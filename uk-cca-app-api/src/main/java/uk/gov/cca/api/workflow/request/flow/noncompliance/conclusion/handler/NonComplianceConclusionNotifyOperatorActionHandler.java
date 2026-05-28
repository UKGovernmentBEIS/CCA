package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceOutcome;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.service.NonComplianceConclusionSubmitService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.validation.NonComplianceConclusionNotifyOperatorValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class NonComplianceConclusionNotifyOperatorActionHandler implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final NonComplianceConclusionSubmitService nonComplianceConclusionSubmitService;
    private final NonComplianceConclusionNotifyOperatorValidator validator;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, NotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate
        validator.validate(requestTask, payload, appUser);

        // Update Request
        final DecisionNotification decisionNotification = payload.getDecisionNotification();
        nonComplianceConclusionSubmitService.notifyOperator(requestTask, decisionNotification);

        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.SUBMITTED,
                        CcaBpmnProcessConstants.IS_NON_COMPLIANCE_REISSUE_PENALTY_NEEDED, false));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_CONCLUSION_NOTIFY_OPERATOR);
    }
}