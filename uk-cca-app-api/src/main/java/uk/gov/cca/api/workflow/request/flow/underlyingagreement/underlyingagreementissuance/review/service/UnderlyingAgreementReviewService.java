package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementApplySavePayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveReviewRequestTaskActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementReviewService {

    @Transactional
    public void saveUnderlyingAgreement(UnderlyingAgreementSaveReviewRequestTaskActionPayload payload,
                                        RequestTask requestTask) {

        UnderlyingAgreementReviewRequestTaskPayload reviewRequestTaskPayload =
                (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        UnderlyingAgreementPayload underlyingAgreementPayload = reviewRequestTaskPayload.getUnderlyingAgreement();
        UnderlyingAgreementApplySavePayload savePayload = payload.getUnderlyingAgreement();

        underlyingAgreementPayload.setUnderlyingAgreementTargetUnitDetails(savePayload.getUnderlyingAgreementTargetUnitDetails());
        underlyingAgreementPayload.getUnderlyingAgreement().setFacilities(savePayload.getFacilities().stream()
                .map(facilityItem -> Facility.builder().status(FacilityStatus.NEW).facilityItem(facilityItem).build())
                .collect(Collectors.toSet()));
        underlyingAgreementPayload.getUnderlyingAgreement().setAuthorisationAndAdditionalEvidence(savePayload.getAuthorisationAndAdditionalEvidence());

        reviewRequestTaskPayload.setUnderlyingAgreement(underlyingAgreementPayload);
        reviewRequestTaskPayload.setSectionsCompleted(payload.getSectionsCompleted());
        reviewRequestTaskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
        reviewRequestTaskPayload.setDetermination(payload.getDetermination());
    }

    @Transactional
    public void saveReviewGroupDecision(final UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload payload,
                                        final RequestTask requestTask) {

        final UnderlyingAgreementReviewGroup group = payload.getGroup();
        final UnderlyingAgreementReviewDecision decision = payload.getDecision();

        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();
        final Map<UnderlyingAgreementReviewGroup, UnderlyingAgreementReviewDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();

        reviewGroupDecisions.put(group, decision);

        final Map<String, String> reviewSectionsCompleted = payload.getReviewSectionsCompleted();
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);

        final Determination determination = payload.getDetermination();
        taskPayload.setDetermination(determination);
    }

    @Transactional
    public void saveFacilityReviewGroupDecision(
            final UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload payload,
            final RequestTask requestTask) {

        final String group = payload.getGroup();
        final UnderlyingAgreementFacilityReviewDecision decision = payload.getDecision();

        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();
        final Map<String, UnderlyingAgreementFacilityReviewDecision> facilityReviewGroupDecisions =
                taskPayload.getFacilitiesReviewGroupDecisions();

        facilityReviewGroupDecisions.put(group, decision);

        final Map<String, String> reviewSectionsCompleted = payload.getReviewSectionsCompleted();
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);

        final Determination determination = payload.getDetermination();
        taskPayload.setDetermination(determination);
    }

    @Transactional
    public void saveDetermination(
            final UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload payload,
            final RequestTask requestTask) {

        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setDetermination(payload.getDetermination());
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final CcaDecisionNotification decisionNotification, AppUser appUser) {
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();

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
        requestPayload.setUnderlyingAgreementProposed(taskPayload.getUnderlyingAgreementProposed());
    }

    @Transactional
    public void requestPeerReview(RequestTask requestTask, final String selectedPeerReviewer,
                                  final String regulatorReviewer) {
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();

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
    public void saveProposedUnderlyingAgreement(final UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload payload,
                                                final RequestTask requestTask) {

        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setUnderlyingAgreementProposed(payload.getUnderlyingAgreementProposed());
    }
}
