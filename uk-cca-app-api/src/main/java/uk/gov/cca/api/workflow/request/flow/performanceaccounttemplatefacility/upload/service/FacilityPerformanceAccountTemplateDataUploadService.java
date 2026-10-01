package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation.FacilityPerformanceAccountTemplateDataUploadValidator;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadService {

    private final FacilityPerformanceAccountTemplateDataUploadValidator facilityPerformanceAccountTemplateDataUploadValidator;
    private final FacilityPerformanceAccountTemplateDataUploadExtractCsvDataService facilityPerformanceAccountTemplateDataUploadExtractCsvService;

    @Transactional
    public void process(RequestTask requestTask, FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload taskActionPayload,
                        LocalDateTime submissionDate) {
        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload =
                (FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();
        FacilityPerformanceAccountTemplateDataUploadRequestMetadata metadata =
                (FacilityPerformanceAccountTemplateDataUploadRequestMetadata) requestTask.getRequest().getMetadata();

        taskPayload.setPerformanceAccountTemplateDataUpload(taskActionPayload.getPerformanceAccountTemplateDataUpload());

        // Validate
        facilityPerformanceAccountTemplateDataUploadValidator.validate(taskPayload, submissionDate.toLocalDate());

        // Extract CSV data
        final List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors = new ArrayList<>();
        Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReportsMap = facilityPerformanceAccountTemplateDataUploadExtractCsvService.exportAndValidateCsvData(taskPayload, csvRowErrors);

        // Set csv extract outcome
        taskPayload.setFacilityReports(facilityReportsMap);
        taskPayload.setCsvRowErrors(csvRowErrors);
        taskPayload.setProcessingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.IN_PROGRESS);

        // Set metadata
        metadata.setSubmittedDate(submissionDate);
    }
}
