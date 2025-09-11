package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.domain.UnderlyingAgreementPeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class UnderlyingAgreementPeerReviewService {

    @Transactional
    public void submitPeerReview(final RequestTask requestTask, final CcaPeerReviewDecisionRequestTaskActionPayload taskActionPayload) {

        final UnderlyingAgreementPeerReviewRequestTaskPayload taskPayload =
                (UnderlyingAgreementPeerReviewRequestTaskPayload) requestTask.getPayload();
        taskPayload.setDecision(taskActionPayload.getDecision());
    }
}