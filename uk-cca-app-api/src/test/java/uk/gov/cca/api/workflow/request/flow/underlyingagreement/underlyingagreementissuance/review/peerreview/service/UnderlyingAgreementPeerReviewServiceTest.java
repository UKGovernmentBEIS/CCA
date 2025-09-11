package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.domain.UnderlyingAgreementPeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecisionType;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementPeerReviewServiceTest {

    @InjectMocks
    private UnderlyingAgreementPeerReviewService underlyingAgreementPeerReviewService;

    @Test
    void submitPeerReview() {

        String filename = "file1";
        UUID fileUuid = UUID.fromString("eb44e8cc-5bef-4550-bb63-e06993797920");
        RequestTask requestTask = RequestTask.builder()
                .payload(UnderlyingAgreementPeerReviewRequestTaskPayload.builder().build())
                .build();

        Set<UUID> files = Set.of(fileUuid);
        CcaPeerReviewDecisionRequestTaskActionPayload taskActionPayload = CcaPeerReviewDecisionRequestTaskActionPayload.builder()
                .decision(CcaPeerReviewDecision.builder()
                        .decision(PeerReviewDecision.builder()
                                .type(PeerReviewDecisionType.AGREE)
                                .notes("blablabla")
                                .build())
                        .files(files)
                        .build())
                .build();

        // invoke
        underlyingAgreementPeerReviewService.submitPeerReview(requestTask, taskActionPayload);

        // verify
        UnderlyingAgreementPeerReviewRequestTaskPayload requestTaskPayload =
                (UnderlyingAgreementPeerReviewRequestTaskPayload) requestTask.getPayload();
        assertThat(requestTaskPayload.getReferencedAttachmentIds()).isEqualTo(files);
        assertThat(requestTaskPayload.getDecision().getDecision().getType()).isEqualTo(PeerReviewDecisionType.AGREE);
        assertThat(requestTaskPayload.getDecision().getDecision().getNotes()).isEqualTo("blablabla");
    }
}
