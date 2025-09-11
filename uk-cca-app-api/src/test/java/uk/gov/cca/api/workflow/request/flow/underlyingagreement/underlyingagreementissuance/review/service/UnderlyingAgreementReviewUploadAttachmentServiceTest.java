package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewUploadAttachmentServiceTest {

	@InjectMocks
    private UnderlyingAgreementReviewUploadAttachmentService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String fileName = "name";
        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .payload(UnderlyingAgreementReviewRequestTaskPayload.builder().build())
                .build();
        String attachmentUuid = UUID.randomUUID().toString();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        service.uploadAttachment(requestTaskId, attachmentUuid, fileName);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);

        assertThat(((UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload()).getReviewAttachments())
                .containsEntry(UUID.fromString(attachmentUuid), fileName);
    }
}
