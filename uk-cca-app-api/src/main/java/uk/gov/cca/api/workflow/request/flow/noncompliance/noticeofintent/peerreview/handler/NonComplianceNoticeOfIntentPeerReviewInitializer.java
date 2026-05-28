package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.peerreview.handler;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.peerreview.domain.NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class NonComplianceNoticeOfIntentPeerReviewInitializer implements InitializeRequestTaskHandler {
    @Override
    public RequestTaskPayload initializePayload(Request request) {

        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        return NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_PAYLOAD)
                .noticeOfIntent(requestPayload.getNoticeOfIntent())
                .sectionsCompleted(requestPayload.getSectionsCompleted())
                .nonComplianceAttachments(requestPayload.getNonComplianceAttachments())
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW);
    }
}
