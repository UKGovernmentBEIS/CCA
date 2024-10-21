package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationRequestTaskPayload;
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
public class UnderlyingAgreementVariationActivationUploadAttachmentServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationActivationUploadAttachmentService underlyingAgreementVariationActivationUploadAttachmentService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void uploadAttachment() {
        final long requestTaskId = 1L;
        final String attachmentUuid = UUID.randomUUID().toString();
        final String filename = "filename";
        Map<UUID, String> attachments = new HashMap<>();
        attachments.put(UUID.randomUUID(), "exist");

        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .payload(UnderlyingAgreementVariationActivationRequestTaskPayload.builder()
                        .underlyingAgreementActivationAttachments(attachments)
                        .build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        underlyingAgreementVariationActivationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // Verify
        assertThat(((UnderlyingAgreementVariationActivationRequestTaskPayload) requestTask.getPayload()).getUnderlyingAgreementActivationAttachments())
                .hasSize(2);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
    }
}
