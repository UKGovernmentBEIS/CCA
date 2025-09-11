package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service.UnderlyingAgreementReviewUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

	private final UnderlyingAgreementReviewUploadAttachmentService underlyingAgreementReviewUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
    	underlyingAgreementReviewUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.UNDERLYING_AGREEMENT_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
