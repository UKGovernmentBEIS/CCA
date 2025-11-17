package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitSaveRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class PreAuditReviewSubmitService {

    @Transactional
    public void applySaveAction(final PreAuditReviewSubmitSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        PreAuditReviewSubmitRequestTaskPayload requestTaskPayload = (PreAuditReviewSubmitRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.setPreAuditReviewDetails(payload.getPreAuditReviewDetails());
        requestTaskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void submitPreAuditReview(RequestTask requestTask) {
        PreAuditReviewSubmitRequestTaskPayload taskPayload = (PreAuditReviewSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        FacilityAuditRequestPayload requestPayload = (FacilityAuditRequestPayload) request.getPayload();

        requestPayload.setPreAuditReviewDetails(taskPayload.getPreAuditReviewDetails());
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setPreAuditReviewAttachments(taskPayload.getFacilityAuditAttachments());
    }
}
