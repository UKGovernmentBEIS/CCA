package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewSavePayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewService {

    @Transactional
    public void saveUnderlyingAgreementVariation(UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload payload,
                                                 RequestTask requestTask) {

        UnderlyingAgreementVariationReviewRequestTaskPayload reviewRequestTaskPayload =
                (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        UnderlyingAgreementVariationPayload underlyingAgreementPayload = reviewRequestTaskPayload.getUnderlyingAgreement();
        UnderlyingAgreementVariationReviewSavePayload savePayload = payload.getUnderlyingAgreement();

        underlyingAgreementPayload.setUnderlyingAgreementVariationDetails(savePayload.getUnderlyingAgreementVariationDetails());
        underlyingAgreementPayload.setUnderlyingAgreementTargetUnitDetails(savePayload.getUnderlyingAgreementTargetUnitDetails());
        underlyingAgreementPayload.getUnderlyingAgreement().setFacilities(savePayload.getFacilities());
        underlyingAgreementPayload.getUnderlyingAgreement().setTargetPeriod5Details(savePayload.getTargetPeriod5Details());
        underlyingAgreementPayload.getUnderlyingAgreement().setTargetPeriod6Details(savePayload.getTargetPeriod6Details());
        underlyingAgreementPayload.getUnderlyingAgreement().setAuthorisationAndAdditionalEvidence(savePayload.getAuthorisationAndAdditionalEvidence());

        reviewRequestTaskPayload.setUnderlyingAgreement(underlyingAgreementPayload);
        reviewRequestTaskPayload.setSectionsCompleted(payload.getSectionsCompleted());
        reviewRequestTaskPayload.setReviewGroupDecisions(payload.getReviewGroupDecisions());
        reviewRequestTaskPayload.setFacilitiesReviewGroupDecisions(payload.getFacilitiesReviewGroupDecisions());
        reviewRequestTaskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
        reviewRequestTaskPayload.setDetermination(payload.getDetermination());
    }

    @Transactional
    public void saveReviewGroupDecision(final UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload payload,
                                        final RequestTask requestTask) {

        final UnderlyingAgreementVariationReviewGroup group = payload.getGroup();
        final UnderlyingAgreementReviewDecision decision = payload.getDecision();

        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();
        final Map<UnderlyingAgreementVariationReviewGroup, UnderlyingAgreementReviewDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();

        reviewGroupDecisions.put(group, decision);

        final Map<String, String> reviewSectionsCompleted = payload.getReviewSectionsCompleted();
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);

        final Determination determination = payload.getDetermination();
        taskPayload.setDetermination(determination);
    }

    @Transactional
    public void saveFacilityReviewGroupDecision(
            final UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload payload,
            final RequestTask requestTask) {

        final String group = payload.getGroup();
        final UnderlyingAgreementVariationFacilityReviewDecision decision = payload.getDecision();

        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();
        final Map<String, UnderlyingAgreementVariationFacilityReviewDecision> facilityReviewGroupDecisions =
                taskPayload.getFacilitiesReviewGroupDecisions();

        facilityReviewGroupDecisions.put(group, decision);

        final Map<String, String> reviewSectionsCompleted = payload.getReviewSectionsCompleted();
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);

        final Determination determination = payload.getDetermination();
        taskPayload.setDetermination(determination);
    }

    @Transactional
    public void saveDetermination(
            final UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload payload,
            final RequestTask requestTask) {

        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setDetermination(payload.getDetermination());
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final CcaDecisionNotification decisionNotification, AppUser appUser) {
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();

        LocalDateTime submissionDate = LocalDateTime.now();

        request.setSubmissionDate(submissionDate);
        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setDecisionNotification(decisionNotification);
        requestPayload.setUnderlyingAgreement(taskPayload.getUnderlyingAgreement());
        requestPayload.setUnderlyingAgreementAttachments(taskPayload.getUnderlyingAgreementAttachments());
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        requestPayload.setReviewGroupDecisions(taskPayload.getReviewGroupDecisions());
        requestPayload.setFacilitiesReviewGroupDecisions(taskPayload.getFacilitiesReviewGroupDecisions());
        requestPayload.setReviewAttachments(taskPayload.getReviewAttachments());
        requestPayload.setDetermination(taskPayload.getDetermination());
        requestPayload.setAccountReferenceData(taskPayload.getAccountReferenceData());
        requestPayload.setUnderlyingAgreementProposed(taskPayload.getProposedUnderlyingAgreement());
    }

    @Transactional
    public void requestPeerReview(RequestTask requestTask, final String selectedPeerReviewer,
                                  final String regulatorReviewer) {
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(regulatorReviewer);
        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setUnderlyingAgreement(taskPayload.getUnderlyingAgreement());
        requestPayload.setUnderlyingAgreementAttachments(taskPayload.getUnderlyingAgreementAttachments());
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        requestPayload.setReviewGroupDecisions(taskPayload.getReviewGroupDecisions());
        requestPayload.setFacilitiesReviewGroupDecisions(taskPayload.getFacilitiesReviewGroupDecisions());
        requestPayload.setReviewAttachments(taskPayload.getReviewAttachments());
        requestPayload.setDetermination(taskPayload.getDetermination());
        requestPayload.setAccountReferenceData(taskPayload.getAccountReferenceData());
    }

    @Transactional
    public void saveProposedUnderlyingAgreement(final UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload payload,
                                                final RequestTask requestTask) {

        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setUnderlyingAgreementProposed(payload.getUnderlyingAgreementProposed());
    }

}
