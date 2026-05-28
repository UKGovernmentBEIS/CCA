package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation.PerformanceDataFacilityViolation;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

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
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

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

        final UnderlyingAgreementContainer una = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder()
                                .facilityItem(FacilityItem.builder()
                                        .facilityId(facilityBusinessId)
                                        .facilityDetails(FacilityDetails.builder()
                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                .build())
                                        .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                                .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                                                        .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                                                                .baselineYear(Year.of(2024))
                                                                .build()))
                                                        .build())
                                                .build())
                                        .build())
                                .build()))
                        .build())
                .build();

        when(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId))
                .thenReturn(una);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityProductsEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementContainerByAccountId(accountId);
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

        final UnderlyingAgreementContainer una = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder()
                                .facilityItem(FacilityItem.builder()
                                        .facilityId(facilityBusinessId)
                                        .facilityDetails(FacilityDetails.builder()
                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                .build())
                                        .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                                .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                                                        .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                                                                .baselineYear(Year.of(2025))
                                                                .build()))
                                                        .build())
                                                .build())
                                        .build())
                                .build()))
                        .build())
                .build();

        when(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId))
                .thenReturn(una);

        // Invoke
        BusinessValidationResult result = validator.validateFacilityProductsEligibility(facility, targetPeriodDetails, submissionDate);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsOnly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE_PRODUCTS.getMessage());
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementContainerByAccountId(accountId);
    }
}
