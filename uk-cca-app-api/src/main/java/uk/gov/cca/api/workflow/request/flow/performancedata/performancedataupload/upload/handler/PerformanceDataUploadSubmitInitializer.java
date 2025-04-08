package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.handler;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class PerformanceDataUploadSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final PerformanceDataUploadRequestPayload requestPayload =
                (PerformanceDataUploadRequestPayload) request.getPayload();

        return PerformanceDataUploadSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PERFORMANCE_DATA_UPLOAD_SUBMIT_PAYLOAD)
                .sectorAssociationInfo(requestPayload.getSectorAssociationInfo())
                .totalFilesUploaded(0)
                .filesSucceeded(0)
                .filesFailed(0)
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.PERFORMANCE_DATA_UPLOAD_SUBMIT);
    }
}
