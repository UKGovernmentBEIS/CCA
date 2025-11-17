package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation.AuditTrackCorrectiveActionsCompleteValidator;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.service.AuditTrackCorrectiveActionsService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.transform.AuditTrackCorrectiveActionsMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditTrackCorrectiveActionsCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final AuditTrackCorrectiveActionsCompleteValidator auditTrackCorrectiveActionsCompleteValidator;
    private final AuditTrackCorrectiveActionsService auditTrackCorrectiveActionsService;

    private static final AuditTrackCorrectiveActionsMapper AUDIT_TRACK_CORRECTIVE_ACTIONS_MAPPER = Mappers.getMapper(AuditTrackCorrectiveActionsMapper.class);

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final AuditTrackCorrectiveActionsRequestTaskPayload taskPayload = (AuditTrackCorrectiveActionsRequestTaskPayload) requestTask.getPayload();

        // Validate data
        auditTrackCorrectiveActionsCompleteValidator.validate(taskPayload);

        // Update Request
        auditTrackCorrectiveActionsService.applyCompleteAction(requestTask);

        // Add submit action request
        addCompletedRequestAction(appUser, taskPayload, requestTask.getRequest());

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId()));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_COMPLETE_APPLICATION);
    }

    private void addCompletedRequestAction(AppUser user, AuditTrackCorrectiveActionsRequestTaskPayload taskPayload, Request request) {
        AuditTrackCorrectiveActionsSubmittedRequestActionPayload actionPayload =
                AUDIT_TRACK_CORRECTIVE_ACTIONS_MAPPER.toAuditTrackCorrectiveActionsSubmittedRequestActionPayload(taskPayload);

        requestService.addActionToRequest(
                request,
                actionPayload,
                CcaRequestActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED,
                user.getUserId());
    }
}
