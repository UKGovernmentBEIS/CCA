package uk.gov.cca.api.workflow.request.flow.common.domain.peerreview;

import java.util.Map;
import java.util.UUID;

public interface CcaPeerReviewDecisionRequestTaskPayload {

    CcaPeerReviewDecision getDecision();

    Map<UUID, String> getPeerReviewAttachments();
}
