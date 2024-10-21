package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.service.UnderlyingAgreementActivationUploadAttachmentService;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementActivationUploadAttachmentHandlerTest {

    @InjectMocks
    private UnderlyingAgreementActivationUploadAttachmentHandler handler;

    @Mock
    private UnderlyingAgreementActivationUploadAttachmentService underlyingAgreementActivationUploadAttachmentService;

    @Test
    void uploadAttachment() {
        final long requestTaskId = 1L;
        final String attachmentUuid = "uuid";
        final String filename = "test.png";

        // Invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // Verify
        verify(underlyingAgreementActivationUploadAttachmentService, times(1))
                .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        assertThat(handler.getType())
                .isEqualTo(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_ACTIVATION_UPLOAD_ATTACHMENT);
    }
}
