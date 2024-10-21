package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;

@Service
public class UnderlyingAgreementVariationActivationService {

    @Transactional
    public void applySaveAction(final UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        UnderlyingAgreementVariationActivationRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationActivationRequestTaskPayload) requestTask.getPayload();

        taskPayload.setUnderlyingAgreementActivationDetails(payload.getUnderlyingAgreementActivationDetails());
        taskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final CcaDecisionNotification decisionNotification) {
        final UnderlyingAgreementVariationActivationRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationActivationRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();

        request.setSubmissionDate(LocalDateTime.now());

        requestPayload.setUnderlyingAgreementActivationDetails(taskPayload.getUnderlyingAgreementActivationDetails());
        requestPayload.setUnderlyingAgreementActivationAttachments(taskPayload.getUnderlyingAgreementActivationAttachments());
        requestPayload.setDecisionNotification(decisionNotification);
    }
}
