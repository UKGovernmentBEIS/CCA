package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.peerreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntent;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.peerreview.domain.NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceNoticeOfIntentPeerReviewInitializerTest {

    @InjectMocks
    private NonComplianceNoticeOfIntentPeerReviewInitializer initializer;

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

        // invoke
        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        // verify
        assertThat(requestTaskPayload.getPayloadType())
                .isEqualTo(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload.class);
        assertThat(((NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload) requestTaskPayload).getNoticeOfIntent())
                .isEqualTo(requestPayload.getNoticeOfIntent());
        assertThat(((NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload) requestTaskPayload).getSectionsCompleted())
                .isEqualTo(sectionsCompleted);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW);
    }
}
