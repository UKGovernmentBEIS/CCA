package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.service.AuditDetailsCorrectiveActionsSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuditDetailsCorrectiveActionsSubmitSaveActionHandler implements RequestTaskActionHandler<AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AuditDetailsCorrectiveActionsSubmitService auditDetailsCorrectiveActionsSubmitService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        auditDetailsCorrectiveActionsSubmitService.applySaveAction(payload, requestTask);

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SAVE_APPLICATION);
    }
}
