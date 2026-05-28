package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.peerreview.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.peerreview.domain.NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class EnforcementResponseNoticePeerReviewService {

    @Transactional
    public void submitPeerReview(final RequestTask requestTask, final CcaPeerReviewDecisionRequestTaskActionPayload taskActionPayload) {

        final NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload taskPayload =
                (NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload) requestTask.getPayload();
        taskPayload.setDecision(taskActionPayload.getDecision());
    }
}
