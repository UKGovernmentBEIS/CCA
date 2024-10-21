package uk.gov.cca.api.workflow.request.flow.admintermination.submit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;

@Service
public class AdminTerminationSubmitService {

    @Transactional
    public void applySaveAction(final AdminTerminationSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        AdminTerminationSubmitRequestTaskPayload adminTerminationSubmitRequestTaskPayload = (AdminTerminationSubmitRequestTaskPayload) requestTask.getPayload();
        adminTerminationSubmitRequestTaskPayload.setAdminTerminationReasonDetails(payload.getAdminTerminationReasonDetails());
        adminTerminationSubmitRequestTaskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final CcaDecisionNotification decisionNotification) {
        final AdminTerminationSubmitRequestTaskPayload taskPayload = (AdminTerminationSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final AdminTerminationRequestPayload requestPayload = (AdminTerminationRequestPayload) request.getPayload();

        LocalDateTime submissionDate = LocalDateTime.now();
        request.setSubmissionDate(submissionDate);

        requestPayload.setSubmitSubmissionDate(submissionDate);
        requestPayload.setAdminTerminationReasonDetails(taskPayload.getAdminTerminationReasonDetails());
        requestPayload.setAdminTerminationSubmitAttachments(taskPayload.getAdminTerminationAttachments());
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setDecisionNotification(decisionNotification);
    }
}
