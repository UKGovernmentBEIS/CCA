package uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityValidatorTest {

    @InjectMocks
    private PerformanceDataFacilityValidator validator;

    @Mock
    private PerformanceDataFacilityReferenceDataService performanceDataFacilityReferenceDataService;

    @Mock
    private PerformanceDataFacilityStatusQueryService performanceDataFacilityStatusQueryService;

    @Test
    void validateReportSubmission_INTERIM_start_of_reporting_valid() {
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

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodDetails, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateReportSubmission_INTERIM_end_of_reporting_valid() {
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

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodDetails, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateReportSubmission_FINAL_end_of_reporting_valid() {
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

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodDetails, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateReportSubmission_FINAL_start_of_reporting_valid() {
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

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodDetails, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateReportSubmission_start_of_reporting_not_valid() {
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

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodDetails, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsExactlyInAnyOrder(
                        PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.TARGET_PERIOD_REPORTING_NOT_STARTED.getMessage(),
                        PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.REPORT_TYPE_NOT_VALID.getMessage());
    }

    @Test
    void validateReportSubmission_end_of_reporting_not_valid() {
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

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodDetails, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsExactlyInAnyOrder(
                        PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.TARGET_PERIOD_REPORTING_IS_ENDED.getMessage(),
                        PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.REPORT_TYPE_NOT_VALID.getMessage());
    }

    @Test
    void validateReportSubmission_FINAL_not_valid() {
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

        // Invoke
        BusinessValidationResult result = validator.validateReportSubmission(targetPeriodDetails, reportType, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.REPORT_TYPE_NOT_VALID.getMessage());
    }

    @Test
    void validateFacilityEligibility() {
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateFacilityEligibility_active_not_valid() {
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsOnly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE.getMessage());
    }

    @Test
    void validateFacilityEligibility_inactive() {
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateFacilityEligibility_inactive_not_valid() {
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsOnly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE.getMessage());
    }

    @Test
    void validateFacilityEligibility_inactive_created_date_invalid() {
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsOnly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE.getMessage());
    }

    @Test
    void validateFacilityEligibility_inactive_closed_date_invalid() {
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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

        // Invoke
        BusinessValidationResult result = validator.validateFacilityEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsOnly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE.getMessage());
    }

    @Test
    void validateFacilityBaselineDateEligibility_valid() {
        final Long accountId = 11L;
        final String facilityBusinessId = "facilityBusinessId";
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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
        final FacilityDTO facility = FacilityDTO.builder().facilityBusinessId(facilityBusinessId).accountId(accountId).build();

        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2024, 1, 1))
                .build();

        when(performanceDataFacilityReferenceDataService.getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024)))
                .thenReturn(baselineAndTargets);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityBaselineDateEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(performanceDataFacilityReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024));
    }

    @Test
    void validateFacilityBaselineDateEligibility_same_date_valid() {
        final Long accountId = 11L;
        final String facilityBusinessId = "facilityBusinessId";
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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
        final FacilityDTO facility = FacilityDTO.builder().facilityBusinessId(facilityBusinessId).accountId(accountId).build();

        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2025, 1, 1))
                .build();

        when(performanceDataFacilityReferenceDataService.getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024)))
                .thenReturn(baselineAndTargets);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityBaselineDateEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(performanceDataFacilityReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024));
    }

    @Test
    void validateFacilityBaselineDateEligibility_not_valid() {
        final Long accountId = 11L;
        final String facilityBusinessId = "facilityBusinessId";
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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
        final FacilityDTO facility = FacilityDTO.builder().facilityBusinessId(facilityBusinessId).accountId(accountId).build();

        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2026, 1, 1))
                .build();

        when(performanceDataFacilityReferenceDataService.getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024)))
                .thenReturn(baselineAndTargets);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityBaselineDateEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsOnly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_BASELINE_DATE_NOT_ELIGIBLE.getMessage());
        verify(performanceDataFacilityReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024));
    }

    @Test
    void validateFacilityReportingLock() {
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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
        final FacilityDTO facility = FacilityDTO.builder().id(1L).build();

        when(performanceDataFacilityStatusQueryService.getLockedStatus(1L, Year.of(2024)))
                .thenReturn(false);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityReportingLock(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(performanceDataFacilityStatusQueryService, times(1)).getLockedStatus(1L, Year.of(2024));
    }

    @Test
    void validateFacilityReportingLock_not_valid() {
        final LocalDate submissionDate = LocalDate.of(2026, 12, 31);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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
        final FacilityDTO facility = FacilityDTO.builder().id(1L).build();

        when(performanceDataFacilityStatusQueryService.getLockedStatus(1L, Year.of(2025)))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityReportingLock(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsOnly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_IS_LOCKED.getMessage());
        verify(performanceDataFacilityStatusQueryService, times(1)).getLockedStatus(1L, Year.of(2025));
    }

    @Test
    void validateFacilityProductsEligibility() {
        final Long accountId = 11L;
        final String facilityBusinessId = "facilityBusinessId";
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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
                .facilityBusinessId(facilityBusinessId)
                .accountId(accountId)
                .build();

        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .baselineYear(Year.of(2024))
                                .build()
                ))
                .build();

        when(performanceDataFacilityReferenceDataService.getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024)))
                .thenReturn(baselineAndTargets);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityProductsEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(performanceDataFacilityReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024));
    }

    @Test
    void validateFacilityProductsEligibility_no_energy_type_valid() {
        final Long accountId = 11L;
        final String facilityBusinessId = "facilityBusinessId";
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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
                .facilityBusinessId(facilityBusinessId)
                .accountId(accountId)
                .build();

        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder().build();

        when(performanceDataFacilityReferenceDataService.getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024)))
                .thenReturn(baselineAndTargets);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityProductsEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(performanceDataFacilityReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024));
    }

    @Test
    void validateFacilityProductsEligibility_products_not_valid() {
        final Long accountId = 11L;
        final String facilityBusinessId = "facilityBusinessId";
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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
                .facilityBusinessId(facilityBusinessId)
                .accountId(accountId)
                .build();

        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .baselineYear(Year.of(2025))
                                .build()
                ))
                .build();

        when(performanceDataFacilityReferenceDataService.getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024)))
                .thenReturn(baselineAndTargets);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityProductsEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsOnly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE_PRODUCTS.getMessage());
        verify(performanceDataFacilityReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024));
    }

    @Test
    void validateFacilityProductsEligibility_empty_products_not_valid() {
        final Long accountId = 11L;
        final String facilityBusinessId = "facilityBusinessId";
        final LocalDate submissionDate = LocalDate.of(2025, 12, 1);
        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
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
                .facilityBusinessId(facilityBusinessId)
                .accountId(accountId)
                .build();

        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .build();

        when(performanceDataFacilityReferenceDataService.getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024)))
                .thenReturn(baselineAndTargets);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityProductsEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsOnly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE_PRODUCTS.getMessage());
        verify(performanceDataFacilityReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, Year.of(2024));
    }
}
