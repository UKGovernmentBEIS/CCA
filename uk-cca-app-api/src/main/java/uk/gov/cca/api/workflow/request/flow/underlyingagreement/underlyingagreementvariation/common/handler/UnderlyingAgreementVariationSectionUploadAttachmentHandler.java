package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationSectionUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationSectionUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final UnderlyingAgreementVariationSectionUploadAttachmentService underlyingAgreementVariationSectionUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        underlyingAgreementVariationSectionUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_UPLOAD_SECTION_ATTACHMENT;
    }
}
