package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementSectionUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        UnderlyingAgreementRequestTaskPayload requestTaskPayload =
                (UnderlyingAgreementRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getUnderlyingAgreementAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
