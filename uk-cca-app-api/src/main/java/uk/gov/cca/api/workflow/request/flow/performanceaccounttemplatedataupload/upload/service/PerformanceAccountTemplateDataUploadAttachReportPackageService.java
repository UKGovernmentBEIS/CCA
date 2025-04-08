package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataUploadAttachReportPackageService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId, final String attachmentUuid, final String filename) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload =
                (PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getUploadAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
