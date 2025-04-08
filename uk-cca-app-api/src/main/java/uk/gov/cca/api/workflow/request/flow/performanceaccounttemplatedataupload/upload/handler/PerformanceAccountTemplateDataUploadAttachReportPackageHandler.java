package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service.PerformanceAccountTemplateDataUploadAttachReportPackageService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataUploadAttachReportPackageHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PerformanceAccountTemplateDataUploadAttachReportPackageService service;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
    	service.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ATTACH_REPORT_PACKAGE;
    }
}
