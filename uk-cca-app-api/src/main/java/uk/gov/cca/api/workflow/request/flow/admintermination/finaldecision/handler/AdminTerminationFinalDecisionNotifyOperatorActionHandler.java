package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationOutcome;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service.AdminTerminationFinalDecisionService;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.validation.AdminTerminationFinalDecisionNotifyOperatorValidator;
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
public class AdminTerminationFinalDecisionNotifyOperatorActionHandler implements RequestTaskActionHandler<CcaNotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AdminTerminationFinalDecisionNotifyOperatorValidator adminTerminationFinalDecisionNotifyOperatorValidator;
    private final AdminTerminationFinalDecisionService adminTerminationFinalDecisionService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                        CcaNotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate
        adminTerminationFinalDecisionNotifyOperatorValidator.validate(requestTask, payload, appUser);

        // Update Request
        final CcaDecisionNotification decisionNotification = payload.getDecisionNotification();
        adminTerminationFinalDecisionService.notifyOperator(requestTask, decisionNotification);

        // Complete task
        final AdminTerminationFinalDecisionRequestTaskPayload taskPayload =
                (AdminTerminationFinalDecisionRequestTaskPayload) requestTask.getPayload();
        final AdminTerminationFinalDecisionType decisionType = taskPayload
                .getAdminTerminationFinalDecisionReasonDetails().getFinalDecisionType();

        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        CcaBpmnProcessConstants.ADMIN_TERMINATION_FINAL_DECISION,
                        decisionType.equals(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                                ? AdminTerminationOutcome.FINALISE
                                : AdminTerminationOutcome.WITHDRAW)
        );
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.ADMIN_TERMINATION_FINAL_DECISION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
