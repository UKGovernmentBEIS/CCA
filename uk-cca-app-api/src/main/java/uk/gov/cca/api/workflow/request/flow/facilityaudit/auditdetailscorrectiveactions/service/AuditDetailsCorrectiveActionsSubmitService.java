package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class AuditDetailsCorrectiveActionsSubmitService {

    @Transactional
    public void applySaveAction(final AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        AuditDetailsCorrectiveActionsSubmitRequestTaskPayload taskPayload = (AuditDetailsCorrectiveActionsSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setAuditDetailsAndCorrectiveActions(payload.getAuditDetailsAndCorrectiveActions());
        taskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(RequestTask requestTask) {
        AuditDetailsCorrectiveActionsSubmitRequestTaskPayload taskPayload = (AuditDetailsCorrectiveActionsSubmitRequestTaskPayload) requestTask.getPayload();

        Request request = requestTask.getRequest();
        FacilityAuditRequestPayload requestPayload = (FacilityAuditRequestPayload) request.getPayload();

        requestPayload.setAuditDetailsAndCorrectiveActions(taskPayload.getAuditDetailsAndCorrectiveActions());
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setAuditDetailsCorrectiveActionsAttachments(taskPayload.getFacilityAuditAttachments());
    }
}
