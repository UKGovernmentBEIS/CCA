package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.validation.PerformanceDataFacilityDataUploadValidator;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadService {

    private final PerformanceDataFacilityDataUploadExtractCsvDataService performanceDataFacilityDataUploadExtractCsvDataService;
    private final PerformanceDataFacilityDataUploadValidator performanceDataFacilityDataUploadValidator;

    @Transactional
    public void process(RequestTask requestTask, PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload taskActionPayload,
                        LocalDateTime submissionDate) {
        PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) requestTask.getPayload();
        PerformanceDataFacilityDataUploadRequestMetadata metadata =
                (PerformanceDataFacilityDataUploadRequestMetadata) requestTask.getRequest().getMetadata();

        taskPayload.setPerformanceDataUpload(taskActionPayload.getPerformanceDataUpload());

        // Validate
        performanceDataFacilityDataUploadValidator.validate(taskPayload, submissionDate.toLocalDate());

        // Extract CSV data
        List<PerformanceDataFacilityCsvErrorEntry> csvRowErrors = new ArrayList<>();
        Map<Long, FacilityUploadReport> facilityReportsMap = performanceDataFacilityDataUploadExtractCsvDataService
                .exportCsvData(taskPayload, csvRowErrors);

        // Set csv extract outcome
        taskPayload.setFacilityReports(facilityReportsMap);
        taskPayload.setCsvRowErrors(csvRowErrors);
        taskPayload.setProcessingStatus(PerformanceDataFacilityDataUploadProcessingStatus.IN_PROGRESS);

        // Set metadata
        metadata.setTargetPeriodType(taskActionPayload.getPerformanceDataUpload().getTargetPeriodType());
        metadata.setReportType(taskActionPayload.getPerformanceDataUpload().getReportType());
        metadata.setSubmittedDate(submissionDate);
    }
}
