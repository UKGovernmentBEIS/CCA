package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service.UnderlyingAgreementSectionUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementSectionUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final UnderlyingAgreementSectionUploadAttachmentService underlyingAgreementSectionUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        underlyingAgreementSectionUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.UNDERLYING_AGREEMENT_UPLOAD_SECTION_ATTACHMENT;
    }
}
