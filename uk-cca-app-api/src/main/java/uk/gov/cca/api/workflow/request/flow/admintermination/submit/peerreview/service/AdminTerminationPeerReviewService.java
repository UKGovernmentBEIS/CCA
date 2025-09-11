package uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.domain.AdminTerminationPeerReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class AdminTerminationPeerReviewService {

    @Transactional
    public void submitPeerReview(final RequestTask requestTask, final CcaPeerReviewDecisionRequestTaskActionPayload taskActionPayload) {

        final AdminTerminationPeerReviewRequestTaskPayload taskPayload =
                (AdminTerminationPeerReviewRequestTaskPayload) requestTask.getPayload();
        taskPayload.setDecision(taskActionPayload.getDecision());
    }
}
