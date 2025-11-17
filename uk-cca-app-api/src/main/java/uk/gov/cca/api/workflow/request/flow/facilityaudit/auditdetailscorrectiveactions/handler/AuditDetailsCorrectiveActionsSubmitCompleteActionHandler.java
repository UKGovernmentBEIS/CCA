package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.service.AuditDetailsCorrectiveActionsSubmitService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.transform.AuditDetailsCorrectiveActionsSubmitMapper;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.validation.AuditDetailsCorrectiveActionsSubmitValidator;
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
public class AuditDetailsCorrectiveActionsSubmitCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final AuditDetailsCorrectiveActionsSubmitService auditDetailsCorrectiveActionsSubmitService;
    private final AuditDetailsCorrectiveActionsSubmitValidator auditDetailsCorrectiveActionsSubmitValidator;

    private static final AuditDetailsCorrectiveActionsSubmitMapper AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_MAPPER = Mappers.getMapper(AuditDetailsCorrectiveActionsSubmitMapper.class);

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final AuditDetailsCorrectiveActionsSubmitRequestTaskPayload taskPayload = (AuditDetailsCorrectiveActionsSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate data
        auditDetailsCorrectiveActionsSubmitValidator.validate(taskPayload);

        // Update Request
        auditDetailsCorrectiveActionsSubmitService.applySubmitAction(requestTask);

        // Add submit action request
        addCompletedRequestAction(appUser, taskPayload, requestTask.getRequest());

        // Complete task
        Boolean isCorrectiveActionsNeeded =
                taskPayload.getAuditDetailsAndCorrectiveActions().getCorrectiveActions().getHasActions();

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        CcaBpmnProcessConstants.FACILITY_AUDIT_OUTCOME, "",
                        CcaBpmnProcessConstants.IS_CORRECTIVE_ACTIONS_NEEDED, isCorrectiveActionsNeeded));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_APPLICATION);
    }

    private void addCompletedRequestAction(AppUser user, AuditDetailsCorrectiveActionsSubmitRequestTaskPayload taskPayload, Request request) {

        AuditDetailsCorrectiveActionsSubmittedRequestActionPayload actionPayload =
                AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_MAPPER.toAuditDetailsCorrectiveActionsSubmittedRequestActionPayload(taskPayload);

        requestService.addActionToRequest(
                request,
                actionPayload,
                CcaRequestActionType.FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED,
                user.getUserId());
    }
}
