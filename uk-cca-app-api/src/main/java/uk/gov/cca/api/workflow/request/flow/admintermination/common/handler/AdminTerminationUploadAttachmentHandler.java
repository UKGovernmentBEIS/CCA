package uk.gov.cca.api.workflow.request.flow.admintermination.common.handler;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.AdminTerminationUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Service
@AllArgsConstructor
public class AdminTerminationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final AdminTerminationUploadAttachmentService adminTerminationUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
    	adminTerminationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.ADMIN_TERMINATION_UPLOAD_ATTACHMENT;
    }
}
