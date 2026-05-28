package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.peerreview.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.peerreview.domain.NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class NoticeOfIntentPeerReviewService {

    @Transactional
    public void submitPeerReview(final RequestTask requestTask, final CcaPeerReviewDecisionRequestTaskActionPayload taskActionPayload) {

        final NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload taskPayload =
                (NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload) requestTask.getPayload();
        taskPayload.setDecision(taskActionPayload.getDecision());
    }
}
