package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.peerreview.handler;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.peerreview.domain.NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class NonComplianceEnforcementResponseNoticePeerReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        return NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEW_PAYLOAD)
                .enforcementResponseNotice(requestPayload.getEnforcementResponseNotice())
                .penaltyReissue(requestPayload.isPenaltyReissueNeeded())
                .sectionsCompleted(requestPayload.getSectionsCompleted())
                .nonComplianceAttachments(requestPayload.getNonComplianceAttachments())
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW);
    }
}
