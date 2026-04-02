package uk.gov.cca.api.workflow.request.flow.noncompliance.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class NonComplianceUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final NonComplianceUploadAttachmentService nonComplianceUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        nonComplianceUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT;
    }
}
