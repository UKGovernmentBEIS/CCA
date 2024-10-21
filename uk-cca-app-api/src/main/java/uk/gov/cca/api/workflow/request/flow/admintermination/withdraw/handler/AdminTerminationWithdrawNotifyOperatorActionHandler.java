package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationOutcome;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.service.AdminTerminationWithdrawService;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.validation.AdminTerminationWithdrawNotifyOperatorValidator;
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
public class AdminTerminationWithdrawNotifyOperatorActionHandler implements RequestTaskActionHandler<CcaNotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AdminTerminationWithdrawNotifyOperatorValidator adminTerminationWithdrawNotifyOperatorValidator;
    private final AdminTerminationWithdrawService adminTerminationWithdrawService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                        CcaNotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate
        adminTerminationWithdrawNotifyOperatorValidator.validate(requestTask, payload, appUser);

        // Update Request
        final CcaDecisionNotification decisionNotification = payload.getDecisionNotification();
        adminTerminationWithdrawService.notifyOperator(requestTask, decisionNotification);

        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId()));
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.ADMIN_TERMINATION_WITHDRAW_NOTIFY_OPERATOR_FOR_DECISION);
    }

}
