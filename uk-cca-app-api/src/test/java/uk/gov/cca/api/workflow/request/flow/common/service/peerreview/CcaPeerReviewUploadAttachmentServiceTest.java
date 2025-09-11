package uk.gov.cca.api.workflow.request.flow.common.service.peerreview;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.domain.AdminTerminationPeerReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaPeerReviewUploadAttachmentServiceTest {

    @InjectMocks
    private CcaPeerReviewUploadAttachmentService peerReviewUploadAttachmentService;

    @Mock
    private RequestTaskService requestTaskService;


    @Test
    void uploadAttachment() {
        final long requestTaskId = 1L;
        final String attachmentUuid = UUID.randomUUID().toString();
        final String filename = "filename";
        final UUID fileUuid = UUID.fromString(attachmentUuid);

        final Map<UUID, String> attachments = new HashMap<>();

        attachments.put(fileUuid, filename);

        final AdminTerminationPeerReviewRequestTaskPayload payload = AdminTerminationPeerReviewRequestTaskPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(payload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // invoke
        peerReviewUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        assertThat(requestTask.getPayload().getAttachments()).isEqualTo(attachments);

    }
}
