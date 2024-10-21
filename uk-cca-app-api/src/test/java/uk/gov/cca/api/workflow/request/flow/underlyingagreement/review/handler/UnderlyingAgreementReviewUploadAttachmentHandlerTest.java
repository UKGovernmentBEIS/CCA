package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service.UnderlyingAgreementReviewUploadAttachmentService;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewUploadAttachmentHandlerTest {

	@InjectMocks
    private UnderlyingAgreementReviewUploadAttachmentHandler handler;

    @Mock
    private UnderlyingAgreementReviewUploadAttachmentService underlyingAgreementReviewUploadAttachmentService;
    
    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "filename";
        String attachmentUuid = UUID.randomUUID().toString();

        //invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(underlyingAgreementReviewUploadAttachmentService, times(1))
                .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getTypes() {
        assertThat(handler.getType()).isEqualTo(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT);
    }
}
