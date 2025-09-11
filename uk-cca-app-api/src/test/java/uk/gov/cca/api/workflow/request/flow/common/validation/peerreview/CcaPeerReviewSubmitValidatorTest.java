package uk.gov.cca.api.workflow.request.flow.common.validation.peerreview;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.domain.AdminTerminationPeerReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecisionType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CcaPeerReviewSubmitValidatorTest {

    @InjectMocks
    private CcaPeerReviewSubmitValidator validator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate() {
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final UUID fileUuid = UUID.randomUUID();
        Set<UUID> files = Set.of(fileUuid);
        Map<UUID, String> attachments = Map.of(fileUuid, "attachedFile");
        final AdminTerminationPeerReviewRequestTaskPayload requestTaskPayload =
                AdminTerminationPeerReviewRequestTaskPayload.builder()
                        .peerReviewAttachments(attachments)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .id(2L)
                .request(request)
                .payload(requestTaskPayload)
                .processTaskId(processTaskId)
                .build();

        CcaPeerReviewDecisionRequestTaskActionPayload payload = CcaPeerReviewDecisionRequestTaskActionPayload.builder()
                .decision(CcaPeerReviewDecision.builder()
                        .files(files)
                        .decision(PeerReviewDecision.builder()
                                .type(PeerReviewDecisionType.AGREE)
                                .build())
                        .build())
                .build();

        when(fileAttachmentsExistenceValidator.valid(payload.getReferencedAttachmentIds(), attachments.keySet())).thenReturn(true);

        // invoke
        validator.validate(requestTask, payload);

        // verify
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(payload.getReferencedAttachmentIds(), attachments.keySet());
    }

    @Test
    void validate_no_files() {
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final UUID fileUuid = UUID.randomUUID();
        Map<UUID, String> attachments = Map.of(fileUuid, "attachedFile");
        final AdminTerminationPeerReviewRequestTaskPayload requestTaskPayload =
                AdminTerminationPeerReviewRequestTaskPayload.builder()
                        .peerReviewAttachments(attachments)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .id(2L)
                .request(request)
                .payload(requestTaskPayload)
                .processTaskId(processTaskId)
                .build();

        CcaPeerReviewDecisionRequestTaskActionPayload payload = CcaPeerReviewDecisionRequestTaskActionPayload.builder()
                .decision(CcaPeerReviewDecision.builder()
                        .decision(PeerReviewDecision.builder()
                                .type(PeerReviewDecisionType.AGREE)
                                .build())
                        .build())
                .build();

        when(fileAttachmentsExistenceValidator.valid(payload.getReferencedAttachmentIds(), attachments.keySet())).thenReturn(true);

        // invoke
        validator.validate(requestTask, payload);

        // verify
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(payload.getReferencedAttachmentIds(), attachments.keySet());
    }

    @Test
    void validate_not_valid() {
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final UUID fileUuid = UUID.randomUUID();
        Set<UUID> files = Set.of(fileUuid);
        Map<UUID, String> attachments = Map.of();
        final AdminTerminationPeerReviewRequestTaskPayload requestTaskPayload =
                AdminTerminationPeerReviewRequestTaskPayload.builder()
                        .peerReviewAttachments(attachments)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .id(2L)
                .request(request)
                .payload(requestTaskPayload)
                .processTaskId(processTaskId)
                .build();

        CcaPeerReviewDecisionRequestTaskActionPayload payload = CcaPeerReviewDecisionRequestTaskActionPayload.builder()
                .decision(CcaPeerReviewDecision.builder()
                        .files(files)
                        .decision(PeerReviewDecision.builder()
                                .type(PeerReviewDecisionType.AGREE)
                                .build())
                        .build())
                .build();

        when(fileAttachmentsExistenceValidator.valid(files, attachments.keySet())).thenReturn(false);

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class, () -> validator.validate(requestTask, payload));

        // verify
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(files, attachments.keySet());
        Assertions.assertEquals(CcaErrorCode.PEER_REVIEW_ATTACHMENT_NOT_FOUND, businessException.getErrorCode());
    }
}
