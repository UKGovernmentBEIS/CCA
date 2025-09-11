package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.peerreview.CcaPeerReviewUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Service
@AllArgsConstructor
public class CcaPeerReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final CcaPeerReviewUploadAttachmentService peerReviewUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        peerReviewUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.PEER_REVIEW_UPLOAD_ATTACHMENT;
    }
}
