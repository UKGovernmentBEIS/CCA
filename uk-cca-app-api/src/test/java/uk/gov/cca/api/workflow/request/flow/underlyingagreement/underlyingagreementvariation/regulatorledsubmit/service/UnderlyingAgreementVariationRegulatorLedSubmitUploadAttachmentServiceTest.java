package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitUploadAttachmentServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitUploadAttachmentService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String fileName = "name";
        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .payload(UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder().build())
                .build();
        String attachmentUuid = UUID.randomUUID().toString();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        service.uploadAttachment(requestTaskId, attachmentUuid, fileName);

        // Verify
        assertThat(((UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload) requestTask.getPayload()).getRegulatorLedSubmitAttachments())
                .containsEntry(UUID.fromString(attachmentUuid), fileName);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
    }
}
