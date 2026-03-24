package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormValidatorTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormValidator validator;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Test
    void validateReportSubmission_INTERIM_start_of_reporting_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.INTERIM;
        final LocalDate submissionDate = LocalDate.of(2005, 1, 1);

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2004))
                                        .reportingStartDate(LocalDate.of(2005, 1, 1))
                                        .reportingEndDate(LocalDate.of(2005, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2005))
                                        .reportingStartDate(LocalDate.of(2006, 1, 1))
                                        .build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodDetails);

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodType, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
    }

    @Test
    void validateReportSubmission_INTERIM_end_of_reporting_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.INTERIM;
        final LocalDate submissionDate = LocalDate.of(2005, 12, 31);

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2004))
                                        .reportingStartDate(LocalDate.of(2005, 1, 1))
                                        .reportingEndDate(LocalDate.of(2005, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2005))
                                        .reportingStartDate(LocalDate.of(2006, 1, 1))
                                        .build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodDetails);

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodType, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
    }

    @Test
    void validateReportSubmission_FINAL_end_of_reporting_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final LocalDate submissionDate = LocalDate.of(2005, 12, 31);

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2004))
                                        .reportingStartDate(LocalDate.of(2005, 1, 1))
                                        .reportingEndDate(LocalDate.of(2005, 12, 31))
                                        .build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodDetails);

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodType, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
    }

    @Test
    void validateReportSubmission_FINAL_start_of_reporting_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final LocalDate submissionDate = LocalDate.of(2006, 1, 1);

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2004))
                                        .reportingStartDate(LocalDate.of(2005, 1, 1))
                                        .reportingEndDate(LocalDate.of(2005, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2005))
                                        .reportingStartDate(LocalDate.of(2006, 1, 1))
                                        .build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodDetails);

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodType, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
    }

    @Test
    void validateReportSubmission_start_of_reporting_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.INTERIM;
        final LocalDate submissionDate = LocalDate.of(2004, 12, 31);

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2004))
                                        .reportingStartDate(LocalDate.of(2005, 1, 1))
                                        .reportingEndDate(LocalDate.of(2005, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2005))
                                        .reportingStartDate(LocalDate.of(2006, 1, 1))
                                        .build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodDetails);

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodType, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityDigitalFormViolation>) result.getViolations()).extracting(PerformanceDataFacilityDigitalFormViolation::getMessage)
                .containsExactlyInAnyOrder(
                        PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage.TARGET_PERIOD_REPORTING_NOT_STARTED.getMessage(),
                        PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage.REPORT_TYPE_NOT_VALID.getMessage());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
    }

    @Test
    void validateReportSubmission_end_of_reporting_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final LocalDate submissionDate = LocalDate.of(2007, 1, 1);

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2004))
                                        .reportingStartDate(LocalDate.of(2005, 1, 1))
                                        .reportingEndDate(LocalDate.of(2005, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2005))
                                        .reportingStartDate(LocalDate.of(2006, 1, 1))
                                        .reportingEndDate(LocalDate.of(2006, 12, 31))
                                        .build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodDetails);

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodType, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityDigitalFormViolation>) result.getViolations()).extracting(PerformanceDataFacilityDigitalFormViolation::getMessage)
                .containsExactlyInAnyOrder(
                        PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage.TARGET_PERIOD_REPORTING_IS_ENDED.getMessage(),
                        PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage.REPORT_TYPE_NOT_VALID.getMessage());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
    }

    @Test
    void validateReportSubmission_FINAL_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final LocalDate submissionDate = LocalDate.of(2005, 2, 1);

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2004))
                                        .reportingStartDate(LocalDate.of(2005, 1, 1))
                                        .reportingEndDate(LocalDate.of(2005, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2005))
                                        .reportingStartDate(LocalDate.of(2006, 1, 1))
                                        .build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodDetails);

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodType, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityDigitalFormViolation>) result.getViolations()).extracting(PerformanceDataFacilityDigitalFormViolation::getMessage)
                .containsExactly(PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage.REPORT_TYPE_NOT_VALID.getMessage());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
    }

    @Test
    void validateFacilityEligibility() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2024))
                                        .reportingStartDate(LocalDate.of(2025, 1, 1))
                                        .reportingEndDate(LocalDate.of(2025, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2025))
                                        .reportingStartDate(LocalDate.of(2026, 1, 1))
                                        .build()
                        ))
                        .build())
                .secondaryReportingStartDate(LocalDate.of(2026, 7, 2))
                .build();

        final FacilityDTO facility = FacilityDTO.builder()
                .createdDate(LocalDate.of(2024, 12, 31).atStartOfDay())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId))
                .thenReturn(facility);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facilityId, targetPeriodType, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1))
                .getFacilityInfoData(facilityId);
    }

    @Test
    void validateFacilityEligibility_active_not_valid() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2024))
                                        .reportingStartDate(LocalDate.of(2025, 1, 1))
                                        .reportingEndDate(LocalDate.of(2025, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2025))
                                        .reportingStartDate(LocalDate.of(2026, 1, 1))
                                        .build()
                        ))
                        .build())
                .secondaryReportingStartDate(LocalDate.of(2026, 7, 2))
                .build();

        final FacilityDTO facility = FacilityDTO.builder()
                .createdDate(LocalDate.of(2025, 1, 1).atStartOfDay())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId))
                .thenReturn(facility);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facilityId, targetPeriodType, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityDigitalFormViolation>) result.getViolations()).extracting(PerformanceDataFacilityDigitalFormViolation::getMessage)
                .containsOnly(PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage.FACILITY_NOT_ELIGIBLE.getMessage());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1))
                .getFacilityInfoData(facilityId);
    }

    @Test
    void validateFacilityEligibility_inactive() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2024))
                                        .reportingStartDate(LocalDate.of(2025, 1, 1))
                                        .reportingEndDate(LocalDate.of(2025, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2025))
                                        .reportingStartDate(LocalDate.of(2026, 1, 1))
                                        .build()
                        ))
                        .build())
                .secondaryReportingStartDate(LocalDate.of(2026, 7, 2))
                .build();

        final FacilityDTO facility = FacilityDTO.builder()
                .createdDate(LocalDate.of(2024, 12, 31).atStartOfDay())
                .closedDate(LocalDate.of(2025, 2, 2))
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId))
                .thenReturn(facility);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facilityId, targetPeriodType, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1))
                .getFacilityInfoData(facilityId);
    }

    @Test
    void validateFacilityEligibility_inactive_not_valid() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2024))
                                        .reportingStartDate(LocalDate.of(2025, 1, 1))
                                        .reportingEndDate(LocalDate.of(2025, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2025))
                                        .reportingStartDate(LocalDate.of(2026, 1, 1))
                                        .build()
                        ))
                        .build())
                .secondaryReportingStartDate(LocalDate.of(2026, 7, 2))
                .build();

        final FacilityDTO facility = FacilityDTO.builder()
                .createdDate(LocalDate.of(2024, 12, 31).atStartOfDay())
                .closedDate(LocalDate.of(2025, 1, 1))
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId))
                .thenReturn(facility);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facilityId, targetPeriodType, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityDigitalFormViolation>) result.getViolations()).extracting(PerformanceDataFacilityDigitalFormViolation::getMessage)
                .containsOnly(PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage.FACILITY_NOT_ELIGIBLE.getMessage());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1))
                .getFacilityInfoData(facilityId);
    }

    @Test
    void validateFacilityEligibility_inactive_created_date_invalid() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2024))
                                        .reportingStartDate(LocalDate.of(2025, 1, 1))
                                        .reportingEndDate(LocalDate.of(2025, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2025))
                                        .reportingStartDate(LocalDate.of(2026, 1, 1))
                                        .build()
                        ))
                        .build())
                .secondaryReportingStartDate(LocalDate.of(2026, 7, 2))
                .build();

        final FacilityDTO facility = FacilityDTO.builder()
                .createdDate(LocalDate.of(2025, 1, 1).atStartOfDay())
                .closedDate(LocalDate.of(2025, 2, 2))
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId))
                .thenReturn(facility);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facilityId, targetPeriodType, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityDigitalFormViolation>) result.getViolations()).extracting(PerformanceDataFacilityDigitalFormViolation::getMessage)
                .containsOnly(PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage.FACILITY_NOT_ELIGIBLE.getMessage());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1))
                .getFacilityInfoData(facilityId);
    }

    @Test
    void validateFacilityEligibility_inactive_closed_date_invalid() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2024))
                                        .reportingStartDate(LocalDate.of(2025, 1, 1))
                                        .reportingEndDate(LocalDate.of(2025, 12, 31))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2025))
                                        .reportingStartDate(LocalDate.of(2026, 1, 1))
                                        .build()
                        ))
                        .build())
                .secondaryReportingStartDate(LocalDate.of(2026, 7, 2))
                .build();

        final FacilityDTO facility = FacilityDTO.builder()
                .createdDate(LocalDate.of(2024, 1, 1).atStartOfDay())
                .closedDate(LocalDate.of(2024, 2, 2))
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId))
                .thenReturn(facility);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facilityId, targetPeriodType, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityDigitalFormViolation>) result.getViolations()).extracting(PerformanceDataFacilityDigitalFormViolation::getMessage)
                .containsOnly(PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage.FACILITY_NOT_ELIGIBLE.getMessage());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1))
                .getFacilityInfoData(facilityId);
    }
}
