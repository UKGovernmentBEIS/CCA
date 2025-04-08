package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PerformanceDataUploadAttachReportPackageService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId, final String attachmentUuid, final String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        PerformanceDataUploadSubmitRequestTaskPayload requestTaskPayload =
                (PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getPerformanceDataUploadAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
