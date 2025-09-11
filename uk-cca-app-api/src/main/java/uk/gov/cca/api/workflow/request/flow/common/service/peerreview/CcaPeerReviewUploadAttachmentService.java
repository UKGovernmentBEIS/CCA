package uk.gov.cca.api.workflow.request.flow.common.service.peerreview;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CcaPeerReviewUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        CcaPeerReviewDecisionRequestTaskPayload requestTaskPayload =
                (CcaPeerReviewDecisionRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.getPeerReviewAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
