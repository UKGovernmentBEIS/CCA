package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.service.UnderlyingAgreementActivationUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementActivationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final UnderlyingAgreementActivationUploadAttachmentService underlyingAgreementActivationUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        underlyingAgreementActivationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.UNDERLYING_AGREEMENT_ACTIVATION_UPLOAD_ATTACHMENT;
    }
}
