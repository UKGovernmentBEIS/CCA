package uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.handler;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.domain.AdminTerminationPeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class AdminTerminationApplicationPeerReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final AdminTerminationRequestPayload requestPayload = (AdminTerminationRequestPayload) request.getPayload();
        return AdminTerminationPeerReviewRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_PEER_REVIEW_PAYLOAD)
                .adminTerminationReasonDetails(requestPayload.getAdminTerminationReasonDetails())
                .sectionsCompleted(requestPayload.getSectionsCompleted())
                .adminTerminationAttachments(requestPayload.getAdminTerminationSubmitAttachments())
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_PEER_REVIEW);
    }
}
