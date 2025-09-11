package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service.UnderlyingAgreementVariationReviewUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final UnderlyingAgreementVariationReviewUploadAttachmentService underlyingAgreementVariationReviewUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        underlyingAgreementVariationReviewUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
