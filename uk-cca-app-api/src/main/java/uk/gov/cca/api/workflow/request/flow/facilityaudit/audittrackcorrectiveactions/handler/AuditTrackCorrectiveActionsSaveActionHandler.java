package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.service.AuditTrackCorrectiveActionsService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation.AuditTrackCorrectiveActionsFollowUpResponsesValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditTrackCorrectiveActionsSaveActionHandler implements RequestTaskActionHandler<AuditTrackCorrectiveActionsSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AuditTrackCorrectiveActionsService auditTrackCorrectiveActionsService;
    private final WorkflowService workflowService;
    private final AuditTrackCorrectiveActionsFollowUpResponsesValidator auditTrackCorrectiveActionsFollowUpResponsesValidator;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, AuditTrackCorrectiveActionsSaveRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final AuditTrackCorrectiveActionsRequestTaskPayload taskPayload =
                (AuditTrackCorrectiveActionsRequestTaskPayload) requestTask.getPayload();

        // validate the responded reference
        BusinessValidationResult businessValidationResult =
                auditTrackCorrectiveActionsFollowUpResponsesValidator.validateResponseReference(taskPayload, payload.getActionTitle());

        if (businessValidationResult.isValid()) {
            // send a recalculate event only if the corrective action has been completed
            boolean sendRecalculateEvent = taskPayload.getRespondedActions().contains(payload.getActionTitle());

            auditTrackCorrectiveActionsService.applySaveAction(payload, requestTask);

            if (sendRecalculateEvent) {
                // Send message event to trigger due date change
                workflowService.sendEvent(
                        requestTask.getRequest().getId(),
                        CcaBpmnProcessConstants.TIMER_RECALCULATED,
                        Map.of());
            }
        }

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SAVE_RESPONSE);
    }
}
