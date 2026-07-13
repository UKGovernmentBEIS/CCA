package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUploadErrorType;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadCompleteService {

    private final RequestTaskService requestTaskService;
    private final PerformanceDataFacilityDataUploadCreateCsvService performanceDataFacilityDataUploadCreateCsvService;

    @Transactional
    public void processCompleted(String requestId, Map<Long, FacilityUploadReport> facilityReports) {
        RequestTask requestTask = requestTaskService
                .findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT, requestId);
        PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) requestTask.getPayload();
        PerformanceDataFacilityDataUploadRequestMetadata metadata =
                (PerformanceDataFacilityDataUploadRequestMetadata) requestTask.getRequest().getMetadata();

        // Create Summary CSV
        performanceDataFacilityDataUploadCreateCsvService.createCsvFile(requestTask, facilityReports);

        long succeeded = facilityReports.values().stream().filter(FacilityUploadReport::isSucceeded).count();
        long failed = facilityReports.values().stream().filter(acc -> !acc.isSucceeded()).count();

        taskPayload.setFacilityReports(facilityReports);
        taskPayload.setProcessingStatus(PerformanceDataFacilityDataUploadProcessingStatus.COMPLETED);

        // Set results
        taskPayload.getResults().setTotalFilesUploaded(taskPayload.getPerformanceDataUpload().getFiles().size());
        taskPayload.getResults().setFacilitiesSucceeded((int) succeeded);
        taskPayload.getResults().setFacilitiesFailed(taskPayload.getCsvRowErrors().size() + (int) failed);
        taskPayload.getResults().setSubmittedDate(metadata.getSubmittedDate());
    }

    @Transactional
    public void processMessageFailed(String requestId) {
        RequestTask requestTask = requestTaskService
                .findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT, requestId);
        PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setProcessingStatus(PerformanceDataFacilityDataUploadProcessingStatus.COMPLETED);
        taskPayload.setErrorMessage(PerformanceDataFacilityUploadErrorType.MESSAGE_PROCESSING_FAILED);
    }
}
