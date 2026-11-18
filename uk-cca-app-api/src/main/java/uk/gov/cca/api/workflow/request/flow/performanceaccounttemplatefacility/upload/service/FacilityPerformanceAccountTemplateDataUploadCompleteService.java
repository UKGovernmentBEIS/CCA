package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadErrorType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadCompleteService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void processCompleted(String requestId, Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports) {
        RequestTask requestTask = requestTaskService
                .findByTypeAndRequestId(CcaRequestTaskType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId);
        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload =
                (FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();
        FacilityPerformanceAccountTemplateDataUploadRequestMetadata metadata =
                (FacilityPerformanceAccountTemplateDataUploadRequestMetadata) requestTask.getRequest().getMetadata();

        //TODO: Create Summary CSV

        long succeeded = facilityReports.values().stream().filter(FacilityPerformanceAccountTemplateUploadReport::isSucceeded).count();
        long failed = facilityReports.values().stream().filter(acc -> !acc.isSucceeded()).count();

        requestTaskPayload.setFacilityReports(facilityReports);
        requestTaskPayload.setProcessingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);

        //TODO: Set results
    }

    @Transactional
    public void processMessageFailed(String requestId) {
        RequestTask requestTask = requestTaskService
                .findByTypeAndRequestId(CcaRequestTaskType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId);
        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload =
                (FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setProcessingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
        taskPayload.setErrorMessage(FacilityPerformanceAccountTemplateDataUploadErrorType.MESSAGE_PROCESSING_FAILED);
    }

}
