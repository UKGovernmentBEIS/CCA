package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.domain.UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecisionType;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedPeerReviewServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedPeerReviewService underlyingAgreementVariationRegulatorLedPeerReviewService;

    @Test
    void submitPeerReview() {
        final Set<UUID> files = Set.of(UUID.randomUUID());
        final CcaPeerReviewDecision decision = CcaPeerReviewDecision.builder()
                .decision(PeerReviewDecision.builder()
                        .type(PeerReviewDecisionType.AGREE)
                        .notes("notes")
                        .build())
                .files(files)
                .build();
        final CcaPeerReviewDecisionRequestTaskActionPayload taskActionPayload = CcaPeerReviewDecisionRequestTaskActionPayload.builder()
                .decision(decision)
                .build();

        UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        // Invoke
        underlyingAgreementVariationRegulatorLedPeerReviewService.submitPeerReview(requestTask, taskActionPayload);

        // Verify
        assertThat(taskPayload.getReferencedAttachmentIds()).isEqualTo(files);
        assertThat(taskPayload.getDecision()).isEqualTo(decision);
    }
}
