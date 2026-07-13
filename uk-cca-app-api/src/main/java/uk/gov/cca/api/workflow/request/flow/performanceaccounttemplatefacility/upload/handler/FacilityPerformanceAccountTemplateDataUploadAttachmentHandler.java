package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service.FacilityPerformanceAccountTemplateDataUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final FacilityPerformanceAccountTemplateDataUploadAttachmentService performanceAccountTemplateDataUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        performanceAccountTemplateDataUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ATTACHMENT;
    }
}
