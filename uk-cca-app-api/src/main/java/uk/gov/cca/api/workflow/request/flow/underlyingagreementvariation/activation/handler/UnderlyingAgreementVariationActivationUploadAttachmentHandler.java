package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service.UnderlyingAgreementVariationActivationUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final UnderlyingAgreementVariationActivationUploadAttachmentService uploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_UPLOAD_ATTACHMENT;
    }
}
