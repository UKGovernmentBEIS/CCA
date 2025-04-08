package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.handler;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class PerformanceDataDownloadSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final PerformanceDataDownloadRequestPayload requestPayload =
                (PerformanceDataDownloadRequestPayload) request.getPayload();

        return PerformanceDataDownloadSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PERFORMANCE_DATA_DOWNLOAD_SUBMIT_PAYLOAD)
                .sectorAssociationInfo(requestPayload.getSectorAssociationInfo())
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.PERFORMANCE_DATA_DOWNLOAD_SUBMIT);
    }
}
