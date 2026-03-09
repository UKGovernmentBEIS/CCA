package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.service;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSavePayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;

@Service
public class UnderlyingAgreementVariationRegulatorLedSubmitService {

    @Transactional
    public void saveUnderlyingAgreementVariation(UnderlyingAgreementVariationRegulatorLedSaveRequestTaskActionPayload actionPayload,
                                                 RequestTask requestTask) {
        UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload) requestTask.getPayload();

        UnderlyingAgreementVariationPayload underlyingAgreementPayload = taskPayload.getUnderlyingAgreement();
        UnderlyingAgreementVariationRegulatorLedSavePayload savePayload = actionPayload.getUnderlyingAgreement();

        if(!ObjectUtils.isEmpty(savePayload)) {
            underlyingAgreementPayload.setUnderlyingAgreementVariationDetails(savePayload.getUnderlyingAgreementVariationDetails());
            underlyingAgreementPayload.setUnderlyingAgreementTargetUnitDetails(savePayload.getUnderlyingAgreementTargetUnitDetails());
            underlyingAgreementPayload.getUnderlyingAgreement().setFacilities(savePayload.getFacilities());
            underlyingAgreementPayload.getUnderlyingAgreement().setTargetPeriod5Details(savePayload.getTargetPeriod5Details());
            underlyingAgreementPayload.getUnderlyingAgreement().setTargetPeriod6Details(savePayload.getTargetPeriod6Details());
            underlyingAgreementPayload.getUnderlyingAgreement().setAuthorisationAndAdditionalEvidence(savePayload.getAuthorisationAndAdditionalEvidence());

            taskPayload.setFacilityChargeStartDateMap(savePayload.getFacilityChargeStartDateMap());
        }

        taskPayload.setUnderlyingAgreement(underlyingAgreementPayload);
        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
        taskPayload.setDetermination(actionPayload.getDetermination());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final CcaDecisionNotification decisionNotification) {
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();

        LocalDateTime submissionDate = LocalDateTime.now();
        request.setSubmissionDate(submissionDate);

        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setDecisionNotification(decisionNotification);
        requestPayload.setUnderlyingAgreementProposed(taskPayload.getUnderlyingAgreement());
        requestPayload.setUnderlyingAgreementAttachments(taskPayload.getUnderlyingAgreementAttachments());
        requestPayload.setRegulatorLedSubmitAttachments(taskPayload.getRegulatorLedSubmitAttachments());
        requestPayload.setRegulatorLedDetermination(taskPayload.getDetermination());
        requestPayload.setAccountReferenceData(taskPayload.getAccountReferenceData());
        requestPayload.setRegulatorLedFacilityChargeStartDateMap(taskPayload.getFacilityChargeStartDateMap());
    }

    @Transactional
    public void requestPeerReview(RequestTask requestTask, final String selectedPeerReviewer, final String regulatorReviewer) {
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();

        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setUnderlyingAgreementProposed(taskPayload.getUnderlyingAgreement());
        requestPayload.setUnderlyingAgreementAttachments(taskPayload.getUnderlyingAgreementAttachments());
        requestPayload.setRegulatorLedSubmitAttachments(taskPayload.getRegulatorLedSubmitAttachments());
        requestPayload.setRegulatorLedDetermination(taskPayload.getDetermination());
        requestPayload.setAccountReferenceData(taskPayload.getAccountReferenceData());
        requestPayload.setRegulatorLedFacilityChargeStartDateMap(taskPayload.getFacilityChargeStartDateMap());
        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
        requestPayload.setRegulatorReviewer(regulatorReviewer);
    }
}
