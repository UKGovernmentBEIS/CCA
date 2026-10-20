package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.handler;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUpload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class FacilityPerformanceAccountTemplateDataUploadSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final FacilityPerformanceAccountTemplateDataUploadRequestPayload requestPayload =
                (FacilityPerformanceAccountTemplateDataUploadRequestPayload) request.getPayload();

        return FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT_PAYLOAD)
                .sectorAssociationInfo(requestPayload.getSectorAssociationInfo())
                .performanceAccountTemplateDataUpload(FacilityPerformanceAccountTemplateDataUpload.builder()
                        .targetYear(requestPayload.getTargetYear())
                        .build())
                .processingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET)
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT);
    }
}
