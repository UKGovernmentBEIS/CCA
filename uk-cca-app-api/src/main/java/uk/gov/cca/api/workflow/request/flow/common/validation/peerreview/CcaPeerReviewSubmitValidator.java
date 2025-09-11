package uk.gov.cca.api.workflow.request.flow.common.validation.peerreview;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
@RequiredArgsConstructor
public class CcaPeerReviewSubmitValidator {

    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public void validate(final RequestTask requestTask,
                         final CcaPeerReviewDecisionRequestTaskActionPayload payload) {

        CcaPeerReviewDecisionRequestTaskPayload requestTaskPayload =
                (CcaPeerReviewDecisionRequestTaskPayload) requestTask.getPayload();

        // Validate files
        if (!fileAttachmentsExistenceValidator
                .valid(payload.getReferencedAttachmentIds(), requestTaskPayload.getPeerReviewAttachments().keySet())) {
            throw new BusinessException(CcaErrorCode.PEER_REVIEW_ATTACHMENT_NOT_FOUND);
        }
    }
}
