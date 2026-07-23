package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.util.PerformanceDataFacilityUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.validation.PerformanceDataFacilityDataUploadValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadService {

    private final PerformanceDataFacilityDataUploadExtractCsvDataService performanceDataFacilityDataUploadExtractCsvDataService;
    private final PerformanceDataFacilityDataUploadValidator performanceDataFacilityDataUploadValidator;
    private final TargetPeriodService targetPeriodService;

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
        
        // Get target periods data and submission type
        final List<TargetPeriodDetailsDTO> targetPeriods = targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(
                Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
        final TargetPeriodDetailsDTO targetPeriod = targetPeriods.stream()
                .filter(tp -> tp.getBusinessId().equals(taskActionPayload.getPerformanceDataUpload().getTargetPeriodType()))
                .findFirst().orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        final TargetPeriodYear targetPeriodYear = PerformanceDataFacilityUtil
                .getTargetPeriodYearBySubmissionDate(targetPeriod, submissionDate.toLocalDate())
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));
        PerformanceDataSubmissionType submissionType = PerformanceDataFacilityUtil
                .getSubmissionTypeBySubmissionDate(targetPeriod, taskActionPayload.getPerformanceDataUpload().getReportType(), submissionDate.toLocalDate())
                .orElse(null);

        // Extract CSV data
        List<PerformanceDataFacilityCsvErrorEntry> csvRowErrors = new ArrayList<>();
        Map<Long, FacilityUploadReport> facilityReportsMap = performanceDataFacilityDataUploadExtractCsvDataService
                .exportCsvData(taskPayload, targetPeriodYear, csvRowErrors);

        // Set csv extract outcome
        taskPayload.setFacilityReports(facilityReportsMap);
        taskPayload.setCsvRowErrors(csvRowErrors);
        taskPayload.setProcessingStatus(PerformanceDataFacilityDataUploadProcessingStatus.IN_PROGRESS);

        // Set metadata
        metadata.setTargetPeriodType(taskActionPayload.getPerformanceDataUpload().getTargetPeriodType());
        metadata.setReportType(taskActionPayload.getPerformanceDataUpload().getReportType());
        metadata.setSubmissionType(submissionType);
        metadata.setTargetPeriodYear(targetPeriodYear);
        metadata.setTargetPeriods(targetPeriods);
        metadata.setSubmittedDate(submissionDate);
    }
}
