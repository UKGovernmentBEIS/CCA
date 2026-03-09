package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationSectionUploadAttachmentService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitUploadSectionAttachmentHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitUploadSectionAttachmentHandler handler;

    @Mock
    private UnderlyingAgreementVariationSectionUploadAttachmentService underlyingAgreementVariationSectionUploadAttachmentService;

    @Test
    void uploadAttachment() {
        final Long requestTaskId = 1L;
        final String filename = "filename";
        final String attachmentUuid = UUID.randomUUID().toString();

        // Invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // Verify
        verify(underlyingAgreementVariationSectionUploadAttachmentService, times(1))
                .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getTypes() {
        assertThat(handler.getType())
                .isEqualTo(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_UPLOAD_SECTION_ATTACHMENT);
    }
}
