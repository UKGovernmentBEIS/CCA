package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;

@Service
public class UnderlyingAgreementActivationService {

	@Transactional
    public void applySaveAction(final UnderlyingAgreementActivationSaveRequestTaskActionPayload payload, RequestTask requestTask) {
		UnderlyingAgreementActivationRequestTaskPayload taskPayload =
                (UnderlyingAgreementActivationRequestTaskPayload) requestTask.getPayload();

        taskPayload.setUnderlyingAgreementActivationDetails(payload.getUnderlyingAgreementActivationDetails());
        taskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final CcaDecisionNotification decisionNotification) {
        final UnderlyingAgreementActivationRequestTaskPayload taskPayload =
                (UnderlyingAgreementActivationRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();

        request.setSubmissionDate(LocalDateTime.now());

        requestPayload.setUnderlyingAgreementActivationDetails(taskPayload.getUnderlyingAgreementActivationDetails());
        requestPayload.setUnderlyingAgreementActivationAttachments(taskPayload.getUnderlyingAgreementActivationAttachments());
        requestPayload.setDecisionNotification(decisionNotification);
    }
}
