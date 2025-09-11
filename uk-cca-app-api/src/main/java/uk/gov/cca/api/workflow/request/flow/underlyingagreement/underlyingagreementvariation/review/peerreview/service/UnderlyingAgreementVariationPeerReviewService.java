package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.domain.UnderlyingAgreementVariationPeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class UnderlyingAgreementVariationPeerReviewService {

    @Transactional
    public void submitPeerReview(final RequestTask requestTask, final CcaPeerReviewDecisionRequestTaskActionPayload taskActionPayload) {

        final UnderlyingAgreementVariationPeerReviewRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationPeerReviewRequestTaskPayload) requestTask.getPayload();
        taskPayload.setDecision(taskActionPayload.getDecision());
    }
}
