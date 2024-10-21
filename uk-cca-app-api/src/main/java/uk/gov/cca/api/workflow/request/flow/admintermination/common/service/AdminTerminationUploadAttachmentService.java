package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestTaskAttachable;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

@Service
@RequiredArgsConstructor
public class AdminTerminationUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        AdminTerminationRequestTaskAttachable requestTaskPayload =
                (AdminTerminationRequestTaskAttachable) requestTask.getPayload();
        requestTaskPayload.getAdminTerminationAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
