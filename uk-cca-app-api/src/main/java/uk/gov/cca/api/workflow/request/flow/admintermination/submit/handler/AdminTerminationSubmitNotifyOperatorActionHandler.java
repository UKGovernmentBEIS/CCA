package uk.gov.cca.api.workflow.request.flow.admintermination.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationOutcome;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonCategory;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.service.AdminTerminationSubmitService;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.validation.AdminTerminationSubmitNotifyOperatorValidator;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdminTerminationSubmitNotifyOperatorActionHandler implements RequestTaskActionHandler<CcaNotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AdminTerminationSubmitNotifyOperatorValidator adminTerminationSubmitNotifyOperatorValidator;
    private final AdminTerminationSubmitService adminTerminationSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                        CcaNotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate
        adminTerminationSubmitNotifyOperatorValidator.validate(requestTask, payload, appUser);

        // Update Request
        final CcaDecisionNotification decisionNotification = payload.getDecisionNotification();
        adminTerminationSubmitService.notifyOperator(requestTask, decisionNotification);

        // Complete task
        final AdminTerminationSubmitRequestTaskPayload taskPayload = (AdminTerminationSubmitRequestTaskPayload) requestTask.getPayload();
        final AdminTerminationReason terminationReason = taskPayload.getAdminTerminationReasonDetails().getReason();

        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        CcaBpmnProcessConstants.IS_REGULATORY_REASON, terminationReason.getCategory().equals(AdminTerminationReasonCategory.REGULATORY),
                        CcaBpmnProcessConstants.ADMIN_TERMINATION_OUTCOME, AdminTerminationOutcome.NOTIFY_OPERATOR)
        );
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.ADMIN_TERMINATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
