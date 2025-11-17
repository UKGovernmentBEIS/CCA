package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.service.AuditTrackCorrectiveActionsService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation.AuditTrackCorrectiveActionsSubmitValidator;
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
public class AuditTrackCorrectiveActionsSubmitActionHandler implements RequestTaskActionHandler<AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AuditTrackCorrectiveActionsSubmitValidator auditTrackCorrectiveActionsSubmitValidator;
    private final AuditTrackCorrectiveActionsService auditTrackCorrectiveActionsService;
    private final WorkflowService workflowService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // validate
        auditTrackCorrectiveActionsSubmitValidator.validate(requestTask, payload);

        // submit reference
        auditTrackCorrectiveActionsService.applySubmitAction(payload, requestTask);

        // Send message event to trigger due date change
        workflowService.sendEvent(
                requestTask.getRequest().getId(),
                CcaBpmnProcessConstants.TIMER_RECALCULATED,
                Map.of());

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMIT_RESPONSE);
    }
}
