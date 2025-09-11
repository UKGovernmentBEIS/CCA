package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

@ExtendWith(MockitoExtension.class)
class AdminTerminationUploadAttachmentServiceTest {

    @InjectMocks
    private AdminTerminationUploadAttachmentService adminTerminationUploadAttachmentService;

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

        final AdminTerminationSubmitRequestTaskPayload payload = AdminTerminationSubmitRequestTaskPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(payload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // invoke
        adminTerminationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        assertThat(requestTask.getPayload().getAttachments()).isEqualTo(attachments);

    }
}
