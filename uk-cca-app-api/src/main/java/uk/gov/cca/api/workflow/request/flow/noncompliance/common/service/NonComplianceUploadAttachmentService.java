package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestTaskAttachable;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NonComplianceUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        NonComplianceRequestTaskAttachable requestTaskPayload =
                (NonComplianceRequestTaskAttachable) requestTask.getPayload();

        requestTaskPayload.getNonComplianceAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
