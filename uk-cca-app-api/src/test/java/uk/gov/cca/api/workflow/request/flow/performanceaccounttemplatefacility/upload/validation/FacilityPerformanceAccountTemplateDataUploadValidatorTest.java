package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.config.PerformanceAccountTemplateConfig;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUpload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadValidatorTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadValidator validator;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private PerformanceAccountTemplateConfig performanceAccountTemplateConfig;

    @Mock
    private DataValidator<FacilityPerformanceAccountTemplateDataUpload> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void isAvailable_valid() {
        final LocalDate today = LocalDate.now();
        final Year targetYear = Year.of(today.getYear());
        final LocalDate submissionDate = LocalDate.of(targetYear.plusYears(1).getValue(), 1, 1);
        final TargetPeriodDetailsDTO activeTargetPeriod = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(TargetPeriodYear.builder()
                                .targetYear(targetYear)
                                .build()))
                        .build())
                .build();

        when(performanceAccountTemplateConfig.getTargetYear()).thenReturn(targetYear);
        when(performanceAccountTemplateConfig.getSubmissionDate()).thenReturn(submissionDate);
        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(SchemeVersion.CCA_3)).thenReturn(List.of(activeTargetPeriod));


        // invoke
        boolean result = validator.isAvailable();

        // verify
        assertThat(result).isTrue();
        verify(performanceAccountTemplateConfig, times(1)).getTargetYear();
        verify(performanceAccountTemplateConfig, times(1)).getSubmissionDate();
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(SchemeVersion.CCA_3);
    }

    @Test
    void isAvailable_isSubmissionDateValidForReportingPeriod_invalid() {
        final LocalDate today = LocalDate.now();
        final Year targetYear = Year.of(today.getYear());
        final LocalDate submissionDate = LocalDate.of(targetYear.getValue(), 1, 1);

        when(performanceAccountTemplateConfig.getTargetYear()).thenReturn(targetYear);
        when(performanceAccountTemplateConfig.getSubmissionDate()).thenReturn(submissionDate);

        // invoke
        boolean result = validator.isAvailable();

        // verify
        assertThat(result).isFalse();
        verify(performanceAccountTemplateConfig, times(1)).getTargetYear();
        verify(performanceAccountTemplateConfig, times(1)).getSubmissionDate();
        verify(targetPeriodService, never()).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(SchemeVersion.CCA_3);
    }

    @Test
    void isAvailable_isInActiveTargetPeriod_invalid() {
        final LocalDate today = LocalDate.now();
        final Year targetYear = Year.of(today.getYear());
        final LocalDate submissionDate = LocalDate.of(targetYear.plusYears(1).getValue(), 1, 1);
        final TargetPeriodDetailsDTO activeTargetPeriod = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(TargetPeriodYear.builder()
                                .targetYear(targetYear.plusYears(1))
                                .build()))
                        .build())
                .build();
        when(performanceAccountTemplateConfig.getTargetYear()).thenReturn(targetYear);
        when(performanceAccountTemplateConfig.getSubmissionDate()).thenReturn(submissionDate);
        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(SchemeVersion.CCA_3)).thenReturn(List.of(activeTargetPeriod));

        // invoke
        boolean result = validator.isAvailable();

        assertThat(result).isFalse();
        verify(performanceAccountTemplateConfig, times(1)).getTargetYear();
        verify(performanceAccountTemplateConfig, times(1)).getSubmissionDate();
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(SchemeVersion.CCA_3);
    }

    @Test
    void validate_valid() {
        final LocalDate submissionDate = LocalDate.of(2027, 1, 1);
        final Year targetYear = Year.of(submissionDate.getYear() - 1);
        final UUID file = UUID.randomUUID();
        final FacilityPerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = FacilityPerformanceAccountTemplateDataUpload.builder()
                .targetYear(targetYear)
                .files(Set.of(file))
                .build();
        final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
                .processingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET)
                .uploadAttachments(Map.of(file, "csv"))
                .build();

        when(dataValidator.validate(performanceAccountTemplateDataUpload)).thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(file), Set.of(file))).thenReturn(true);

        // invoke
        validator.validate(requestTaskPayload, submissionDate);

        // verify
        verify(dataValidator, times(1)).validate(performanceAccountTemplateDataUpload);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(file), Set.of(file));
    }

    @Test
    void validate_process_status_not_valid() {
        final LocalDate submissionDate = LocalDate.now();
        final Year targetYear = Year.of(submissionDate.getYear() - 1);
        final UUID file = UUID.randomUUID();
        final FacilityPerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = FacilityPerformanceAccountTemplateDataUpload.builder()
                .targetYear(targetYear)
                .files(Set.of(file))
                .build();
        final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
                .processingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.IN_PROGRESS)
                .uploadAttachments(Map.of(file, "csv"))
                .build();

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(requestTaskPayload, submissionDate));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_PROCESS_STATUS);
        verifyNoInteractions(dataValidator, fileAttachmentsExistenceValidator);
    }

    @Test
    void validate_empty_data_not_valid() {
        final LocalDate submissionDate = LocalDate.now();
        final UUID file = UUID.randomUUID();
        final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .processingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET)
                .uploadAttachments(Map.of(file, "csv"))
                .build();

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(requestTaskPayload, submissionDate));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_DATA);
        verifyNoInteractions(dataValidator, fileAttachmentsExistenceValidator);
    }

    @Test
    void validate_data_not_valid() {
        final LocalDate submissionDate = LocalDate.now();
        final UUID file = UUID.randomUUID();
        final FacilityPerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = FacilityPerformanceAccountTemplateDataUpload.builder()
                .targetYear(null)
                .files(Set.of(file))
                .build();
        final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
                .processingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET)
                .uploadAttachments(Map.of(file, "csv"))
                .build();


        when(dataValidator.validate(performanceAccountTemplateDataUpload)).thenReturn(Optional.of(new BusinessViolation()));
        when(fileAttachmentsExistenceValidator.valid(Set.of(file), Set.of(file))).thenReturn(true);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(requestTaskPayload, submissionDate));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_DATA);
        verify(dataValidator, times(1)).validate(performanceAccountTemplateDataUpload);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(file), Set.of(file));
    }

    @Test
    void validate_submission_date_not_valid() {
        final LocalDate submissionDate = LocalDate.of(2027, 1, 1);
        final Year targetYear = Year.of(submissionDate.getYear());
        final UUID file = UUID.randomUUID();
        final FacilityPerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = FacilityPerformanceAccountTemplateDataUpload.builder()
                .targetYear(targetYear)
                .files(Set.of(file))
                .build();
        final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
                .processingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET)
                .uploadAttachments(Map.of(file, "csv"))
                .build();


        when(dataValidator.validate(performanceAccountTemplateDataUpload)).thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(requestTaskPayload, submissionDate));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.EXPIRED_FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_SUBMISSION_DATE);
        verify(dataValidator, times(1)).validate(performanceAccountTemplateDataUpload);
        verify(fileAttachmentsExistenceValidator, never()).valid(Set.of(file), Set.of(file));
    }

}
