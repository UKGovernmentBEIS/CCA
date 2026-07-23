package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUpload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.validation.PerformanceDataFacilityDataUploadValidator;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataUploadServiceTest {

    @InjectMocks
    private PerformanceDataFacilityDataUploadService service;

    @Mock
    private PerformanceDataFacilityDataUploadExtractCsvDataService performanceDataFacilityDataUploadExtractCsvDataService;

    @Mock
    private PerformanceDataFacilityDataUploadValidator performanceDataFacilityDataUploadValidator;
    
    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void process() {
        final LocalDateTime submissionDate = LocalDateTime.now();
        final PerformanceDataFacilityUpload performanceData = PerformanceDataFacilityUpload.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .reportType(PerformanceDataReportType.FINAL)
                .files(Set.of(UUID.randomUUID()))
                .build();
        final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder().build();
        final PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload actionPayload =
                PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload.builder()
                        .performanceDataUpload(performanceData)
                        .build();
        final PerformanceDataFacilityDataUploadRequestMetadata metadata =
                PerformanceDataFacilityDataUploadRequestMetadata.builder().build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().metadata(metadata).build())
                .payload(taskPayload)
                .build();
        final TargetPeriodYear targetPeriodYear = TargetPeriodYear.builder()
                .targetYear(Year.of(2025))
                .reportingStartDate(LocalDate.of(2025, 1, 1))
                .build();
        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .businessId(TargetPeriodType.TP7)
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(targetPeriodYear))
                        .build()
                )
                .secondaryReportingStartDate(LocalDate.of(2025, 7, 1))
                .build();

        final Map<Long, FacilityUploadReport> facilityReportsMap = Map.of(1L, FacilityUploadReport.builder().build());

        when(performanceDataFacilityDataUploadExtractCsvDataService.exportCsvData(eq(taskPayload), eq(targetPeriodYear), anyList()))
                .thenReturn(facilityReportsMap);
        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(
                Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9))).thenReturn(List.of(targetPeriod));

        // Invoke
        service.process(requestTask, actionPayload, submissionDate);

        // Verify
        assertThat(taskPayload.getPerformanceDataUpload()).isEqualTo(performanceData);
        assertThat(taskPayload.getProcessingStatus()).isEqualTo(PerformanceDataFacilityDataUploadProcessingStatus.IN_PROGRESS);
        assertThat(taskPayload.getFacilityReports()).isEqualTo(facilityReportsMap);
        assertThat(metadata.getTargetPeriodType()).isEqualTo(TargetPeriodType.TP7);
        assertThat(metadata.getReportType()).isEqualTo(PerformanceDataReportType.FINAL);
        assertThat(metadata.getSubmittedDate()).isEqualTo(submissionDate);
        assertThat(metadata.getSubmissionType()).isEqualTo(PerformanceDataSubmissionType.SECONDARY);
        assertThat(metadata.getTargetPeriodYear()).isEqualTo(targetPeriodYear);
        assertThat(metadata.getTargetPeriods()).containsExactly(targetPeriod);
        verify(performanceDataFacilityDataUploadValidator, times(1))
                .validate(eq(taskPayload), any());
        verify(performanceDataFacilityDataUploadExtractCsvDataService, times(1))
                .exportCsvData(eq(taskPayload), eq(targetPeriodYear), anyList());
        verify(targetPeriodService, times(1))
        		.getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
    }
}
