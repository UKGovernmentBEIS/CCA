package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service.UnderlyingAgreementVariationActivationUploadAttachmentService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UnderlyingAgreementVariationActivationUploadAttachmentHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationActivationUploadAttachmentHandler handler;

    @Mock
    private UnderlyingAgreementVariationActivationUploadAttachmentService uploadAttachmentService;

    @Test
    void uploadAttachment() {
        final long requestTaskId = 1L;
        final String attachmentUuid = "uuid";
        final String filename = "test.png";

        // Invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // Verify
        verify(uploadAttachmentService, times(1))
                .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        assertThat(handler.getType())
                .isEqualTo(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_UPLOAD_ATTACHMENT);
    }
}
