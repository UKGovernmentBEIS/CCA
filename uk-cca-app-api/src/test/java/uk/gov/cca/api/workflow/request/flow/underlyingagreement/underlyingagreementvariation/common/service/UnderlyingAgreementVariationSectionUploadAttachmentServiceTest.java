package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;
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
class UnderlyingAgreementVariationSectionUploadAttachmentServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationSectionUploadAttachmentService underlyingAgreementVariationSectionUploadAttachmentService;

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
                .payload(UnderlyingAgreementVariationRequestTaskPayload.builder()
                        .underlyingAgreementAttachments(attachments)
                        .build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        underlyingAgreementVariationSectionUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // Verify
        assertThat(((UnderlyingAgreementVariationRequestTaskPayload) requestTask.getPayload()).getUnderlyingAgreementAttachments())
                .hasSize(2);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
    }
}