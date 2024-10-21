package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;

@Service
public class AdminTerminationFinalDecisionService {

    @Transactional
    public void applySaveAction(final AdminTerminationFinalDecisionSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        AdminTerminationFinalDecisionRequestTaskPayload taskPayload =
                (AdminTerminationFinalDecisionRequestTaskPayload) requestTask.getPayload();

        taskPayload.setAdminTerminationFinalDecisionReasonDetails(payload.getAdminTerminationFinalDecisionReasonDetails());
        taskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final CcaDecisionNotification decisionNotification) {
        final AdminTerminationFinalDecisionRequestTaskPayload taskPayload =
                (AdminTerminationFinalDecisionRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final AdminTerminationRequestPayload requestPayload = (AdminTerminationRequestPayload) request.getPayload();

        request.setSubmissionDate(LocalDateTime.now());

        requestPayload.setAdminTerminationFinalDecisionReasonDetails(taskPayload.getAdminTerminationFinalDecisionReasonDetails());
        requestPayload.setAdminTerminationFinalDecisionAttachments(taskPayload.getAdminTerminationAttachments());
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setDecisionNotification(decisionNotification);
    }
}
