package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;

@Service
public class AdminTerminationWithdrawService {

    @Transactional
    public void applySaveAction(final AdminTerminationWithdrawSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        AdminTerminationWithdrawRequestTaskPayload taskPayload =
                (AdminTerminationWithdrawRequestTaskPayload) requestTask.getPayload();

        taskPayload.setAdminTerminationWithdrawReasonDetails(payload.getAdminTerminationWithdrawReasonDetails());
        taskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final CcaDecisionNotification decisionNotification) {
        final AdminTerminationWithdrawRequestTaskPayload taskPayload = (AdminTerminationWithdrawRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final AdminTerminationRequestPayload requestPayload = (AdminTerminationRequestPayload) request.getPayload();

        request.setSubmissionDate(LocalDateTime.now());

        requestPayload.setAdminTerminationWithdrawReasonDetails(taskPayload.getAdminTerminationWithdrawReasonDetails());
        requestPayload.setAdminTerminationWithdrawAttachments(taskPayload.getAdminTerminationAttachments());
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setDecisionNotification(decisionNotification);
    }

}
