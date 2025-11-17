package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.FacilityAuditUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class FacilityAuditUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final FacilityAuditUploadAttachmentService facilityAuditUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        facilityAuditUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.FACILITY_AUDIT_UPLOAD_ATTACHMENT;
    }
}
