package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service.UnderlyingAgreementVariationReviewUploadAttachmentService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationReviewUploadAttachmentHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewUploadAttachmentHandler handler;

    @Mock
    private UnderlyingAgreementVariationReviewUploadAttachmentService underlyingAgreementVariationReviewUploadAttachmentService
            ;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "filename";
        String attachmentUuid = UUID.randomUUID().toString();

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(underlyingAgreementVariationReviewUploadAttachmentService, times(1))
                .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getTypes() {
        assertThat(handler.getType()).isEqualTo(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT);
    }
}