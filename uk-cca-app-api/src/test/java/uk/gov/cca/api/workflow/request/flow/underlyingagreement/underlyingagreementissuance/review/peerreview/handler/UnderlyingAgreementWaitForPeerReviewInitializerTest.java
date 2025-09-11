package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementWaitForPeerReviewInitializerTest {

    @InjectMocks
    UnderlyingAgreementWaitForPeerReviewInitializer initializer;

    @Test
    void initializePayload() {
        Map<String, String> sectionsCompleted = Map.of("sectionA", "COMPLETED");
        UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder().build())
                .determination(Determination.builder().type(DeterminationType.ACCEPTED).reason("bla bla").build())
                .sectionsCompleted(sectionsCompleted)
                .build();

        final Request request = Request.builder()
                .id("request-id")
                .payload(requestPayload)
                .build();

        // Invoke
        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload.getPayloadType())
                .isEqualTo(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(UnderlyingAgreementReviewRequestTaskPayload.class);
        assertThat(((UnderlyingAgreementReviewRequestTaskPayload) requestTaskPayload).getDetermination())
                .isEqualTo(requestPayload.getDetermination());
        assertThat(((UnderlyingAgreementReviewRequestTaskPayload) requestTaskPayload).getSectionsCompleted())
                .isEqualTo(sectionsCompleted);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.UNDERLYING_AGREEMENT_WAIT_FOR_PEER_REVIEW);
    }
}
