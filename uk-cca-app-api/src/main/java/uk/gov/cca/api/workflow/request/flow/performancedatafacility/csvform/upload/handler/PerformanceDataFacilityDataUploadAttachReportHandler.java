package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service.PerformanceDataFacilityDataUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadAttachReportHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PerformanceDataFacilityDataUploadAttachmentService performanceDataUploadAttachReportPackageService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        performanceDataUploadAttachReportPackageService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_ATTACH_REPORT;
    }
}
