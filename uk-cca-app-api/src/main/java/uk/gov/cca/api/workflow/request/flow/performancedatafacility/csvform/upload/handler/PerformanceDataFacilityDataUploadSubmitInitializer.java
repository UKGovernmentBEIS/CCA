package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.handler;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUploadResults;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class PerformanceDataFacilityDataUploadSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final PerformanceDataFacilityDataUploadRequestPayload requestPayload =
                (PerformanceDataFacilityDataUploadRequestPayload) request.getPayload();

        return PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT_PAYLOAD)
                .sectorAssociationInfo(requestPayload.getSectorAssociationInfo())
                .processingStatus(PerformanceDataFacilityDataUploadProcessingStatus.NOT_STARTED_YET)
                .results(PerformanceDataFacilityUploadResults.builder().build())
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT);
    }
}
