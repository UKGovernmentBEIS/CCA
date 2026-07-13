package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityValidator;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUpload;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataUploadValidatorTest {

    @InjectMocks
    private PerformanceDataFacilityDataUploadValidator validator;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private DataValidator<PerformanceDataFacilityUpload> dataValidator;

    @Mock
    private PerformanceDataFacilityValidator performanceDataFacilityValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final UUID file = UUID.randomUUID();
        final PerformanceDataFacilityUpload performanceData = PerformanceDataFacilityUpload.builder()
                .targetPeriodType(targetPeriodType)
                .reportType(reportType)
                .files(Set.of(file))
                .build();
        final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                        .processingStatus(PerformanceDataFacilityDataUploadProcessingStatus.NOT_STARTED_YET)
                        .performanceDataUpload(performanceData)
                        .uploadAttachments(Map.of(file, "csv"))
                        .build();
        final LocalDate submissionDate = LocalDate.of(2020, 1, 1);

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();

        when(dataValidator.validate(performanceData)).thenReturn(Optional.empty());
        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(performanceDataFacilityValidator.validateReportSubmission(targetPeriod, reportType, submissionDate))
                .thenReturn(BusinessValidationResult.valid());
        when(fileAttachmentsExistenceValidator.valid(Set.of(file), Set.of(file))).thenReturn(true);

        // Invoke
        validator.validate(taskPayload, submissionDate);

        // Verify
        verify(dataValidator, times(1)).validate(performanceData);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(performanceDataFacilityValidator, times(1))
                .validateReportSubmission(targetPeriod, reportType, submissionDate);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(file), Set.of(file));
    }

    @Test
    void validate_process_status_not_valid() {
        final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                        .processingStatus(PerformanceDataFacilityDataUploadProcessingStatus.IN_PROGRESS)
                        .build();
        final LocalDate submissionDate = LocalDate.of(2020, 1, 1);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(taskPayload, submissionDate));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_UPLOAD_PROCESS_STATUS);
        verifyNoInteractions(dataValidator, targetPeriodService, performanceDataFacilityValidator,
                fileAttachmentsExistenceValidator);
    }

    @Test
    void validate_empty_data_not_valid() {
        final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                        .processingStatus(PerformanceDataFacilityDataUploadProcessingStatus.NOT_STARTED_YET)
                        .build();
        final LocalDate submissionDate = LocalDate.of(2020, 1, 1);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(taskPayload, submissionDate));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_UPLOAD_DATA);
        verifyNoInteractions(dataValidator, targetPeriodService, performanceDataFacilityValidator,
                fileAttachmentsExistenceValidator);
    }

    @Test
    void validate_data_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final UUID file = UUID.randomUUID();
        final PerformanceDataFacilityUpload performanceData = PerformanceDataFacilityUpload.builder()
                .targetPeriodType(targetPeriodType)
                .reportType(reportType)
                .files(Set.of(file))
                .build();
        final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                        .processingStatus(PerformanceDataFacilityDataUploadProcessingStatus.NOT_STARTED_YET)
                        .performanceDataUpload(performanceData)
                        .uploadAttachments(Map.of(file, "csv"))
                        .build();
        final LocalDate submissionDate = LocalDate.of(2020, 1, 1);

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();

        when(dataValidator.validate(performanceData)).thenReturn(Optional.of(new BusinessViolation()));
        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(performanceDataFacilityValidator.validateReportSubmission(targetPeriod, reportType, submissionDate))
                .thenReturn(BusinessValidationResult.valid());
        when(fileAttachmentsExistenceValidator.valid(Set.of(file), Set.of(file))).thenReturn(true);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(taskPayload, submissionDate));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_UPLOAD_DATA);
        verify(dataValidator, times(1)).validate(performanceData);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(performanceDataFacilityValidator, times(1))
                .validateReportSubmission(targetPeriod, reportType, submissionDate);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(file), Set.of(file));
    }

    @Test
    void validate_report_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final UUID file = UUID.randomUUID();
        final PerformanceDataFacilityUpload performanceData = PerformanceDataFacilityUpload.builder()
                .targetPeriodType(targetPeriodType)
                .reportType(reportType)
                .files(Set.of(file))
                .build();
        final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                        .processingStatus(PerformanceDataFacilityDataUploadProcessingStatus.NOT_STARTED_YET)
                        .performanceDataUpload(performanceData)
                        .uploadAttachments(Map.of(file, "csv"))
                        .build();
        final LocalDate submissionDate = LocalDate.of(2020, 1, 1);

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();

        when(dataValidator.validate(performanceData)).thenReturn(Optional.empty());
        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(performanceDataFacilityValidator.validateReportSubmission(targetPeriod, reportType, submissionDate))
                .thenReturn(BusinessValidationResult.invalid(List.of(
                        new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.TARGET_PERIOD_REPORTING_NOT_STARTED))));
        when(fileAttachmentsExistenceValidator.valid(Set.of(file), Set.of(file))).thenReturn(true);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(taskPayload, submissionDate));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_UPLOAD_DATA);
        verify(dataValidator, times(1)).validate(performanceData);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(performanceDataFacilityValidator, times(1))
                .validateReportSubmission(targetPeriod, reportType, submissionDate);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(file), Set.of(file));
    }

    @Test
    void validate_file_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final UUID file = UUID.randomUUID();
        final PerformanceDataFacilityUpload performanceData = PerformanceDataFacilityUpload.builder()
                .targetPeriodType(targetPeriodType)
                .reportType(reportType)
                .files(Set.of(file))
                .build();
        final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                        .processingStatus(PerformanceDataFacilityDataUploadProcessingStatus.NOT_STARTED_YET)
                        .performanceDataUpload(performanceData)
                        .uploadAttachments(Map.of(file, "csv"))
                        .build();
        final LocalDate submissionDate = LocalDate.of(2020, 1, 1);

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();

        when(dataValidator.validate(performanceData)).thenReturn(Optional.empty());
        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(performanceDataFacilityValidator.validateReportSubmission(targetPeriod, reportType, submissionDate))
                .thenReturn(BusinessValidationResult.valid());
        when(fileAttachmentsExistenceValidator.valid(Set.of(file), Set.of(file))).thenReturn(false);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(taskPayload, submissionDate));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_UPLOAD_DATA);
        verify(dataValidator, times(1)).validate(performanceData);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(performanceDataFacilityValidator, times(1))
                .validateReportSubmission(targetPeriod, reportType, submissionDate);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(file), Set.of(file));
    }
}
