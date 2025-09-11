package uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminTerminationWaitForPeerReviewInitializerTest {

    @InjectMocks
    AdminTerminationWaitForPeerReviewInitializer initializer;

    @Test
    void initializePayload() {
        Map<String, String> sectionsCompleted = Map.of("sectionA", "COMPLETED");
        AdminTerminationRequestPayload requestPayload = AdminTerminationRequestPayload.builder()
                .adminTerminationReasonDetails(AdminTerminationReasonDetails.builder()
                        .reason(AdminTerminationReason.FAILURE_TO_COMPLY)
                        .explanation("bla bla bla bla bla")
                        .build())
                .sectionsCompleted(sectionsCompleted)
                .build();

        final Request request = Request.builder()
                .id("request-id")
                .payload(requestPayload)
                .build();

        // Invoke
        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload.getPayloadType())
                .isEqualTo(CcaRequestTaskPayloadType.ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(AdminTerminationSubmitRequestTaskPayload.class);
        assertThat(((AdminTerminationSubmitRequestTaskPayload) requestTaskPayload).getAdminTerminationReasonDetails())
                .isEqualTo(requestPayload.getAdminTerminationReasonDetails());
        assertThat(((AdminTerminationSubmitRequestTaskPayload) requestTaskPayload).getSectionsCompleted())
                .isEqualTo(sectionsCompleted);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW);
    }

}
