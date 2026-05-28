package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.peerreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntent;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceNoticeOfIntentWaitForPeerReviewInitializerTest {

    @InjectMocks
    private NonComplianceNoticeOfIntentWaitForPeerReviewInitializer initializer;

    @Test
    void initializePayload() {
        final Map<String, String> sectionsCompleted = Map.of("sectionA", "COMPLETED");
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .noticeOfIntent(NonComplianceNoticeOfIntent.builder()
                        .file(UUID.randomUUID())
                        .comments("bla bla bla")
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
                .isEqualTo(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(NonComplianceNoticeOfIntentSubmitRequestTaskPayload.class);
        assertThat(((NonComplianceNoticeOfIntentSubmitRequestTaskPayload) requestTaskPayload).getNoticeOfIntent())
                .isEqualTo(requestPayload.getNoticeOfIntent());
        assertThat(((NonComplianceNoticeOfIntentSubmitRequestTaskPayload) requestTaskPayload).getSectionsCompleted())
                .isEqualTo(sectionsCompleted);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW);
    }
}
