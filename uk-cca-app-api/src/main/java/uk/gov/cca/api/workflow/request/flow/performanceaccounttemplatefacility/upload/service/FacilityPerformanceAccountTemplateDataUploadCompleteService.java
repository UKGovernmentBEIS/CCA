package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadErrorType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadCompleteService {

    private final RequestTaskService requestTaskService;
    private final FacilityPerformanceAccountTemplateDataUploadCreateCsvService createCsvService;

    @Transactional
    public void processCompleted(String requestId, Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports) {
        RequestTask requestTask = requestTaskService
                .findByTypeAndRequestId(CcaRequestTaskType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId);
        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload =
                (FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();
        FacilityPerformanceAccountTemplateDataUploadRequestMetadata metadata =
                (FacilityPerformanceAccountTemplateDataUploadRequestMetadata) requestTask.getRequest().getMetadata();

        // Create Summary CSV
        createCsvService.createCsvFile(requestTask, facilityReports);

        requestTaskPayload.setFacilityReports(facilityReports);
        requestTaskPayload.setProcessingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);

        // Set results
        long succeeded = facilityReports.values().stream().filter(FacilityPerformanceAccountTemplateUploadReport::isSucceeded).count();
        int failed = getFailedFacilities(requestTaskPayload.getCsvRowErrors(), facilityReports);

        requestTaskPayload.getResults().setTotalFilesUploaded(requestTaskPayload.getPerformanceAccountTemplateDataUpload().getFiles().size());
        requestTaskPayload.getResults().setFacilitiesSucceeded((int) succeeded);
        requestTaskPayload.getResults().setFacilitiesFailed(failed);
        requestTaskPayload.getResults().setSubmittedDate(metadata.getSubmittedDate());
    }

    @Transactional
    public void processMessageFailed(String requestId) {
        RequestTask requestTask = requestTaskService
                .findByTypeAndRequestId(CcaRequestTaskType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId);
        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload =
                (FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setProcessingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
        requestTaskPayload.setErrorMessage(FacilityPerformanceAccountTemplateDataUploadErrorType.MESSAGE_PROCESSING_FAILED);
    }

    private int getFailedFacilities(List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors, Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports) {
        long failedUponSubmission = facilityReports.values().stream().filter(acc -> !acc.isSucceeded()).count();

        long nullFacilityBusinessIds = csvRowErrors.stream()
                .map(FacilityPerformanceAccountTemplateDataCsvErrorEntry::getFacilityBusinessId)
                .filter(Objects::isNull)
                .count();

        long distinctFacilityBusinessIds = csvRowErrors.stream()
                .map(FacilityPerformanceAccountTemplateDataCsvErrorEntry::getFacilityBusinessId)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        return (int) (failedUponSubmission + nullFacilityBusinessIds + distinctFacilityBusinessIds);
    }
}