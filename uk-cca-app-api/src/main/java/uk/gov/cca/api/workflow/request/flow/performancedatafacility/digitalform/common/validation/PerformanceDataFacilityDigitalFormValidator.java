package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.util.PerformanceDataFacilityUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.utils.UnderlyingAgreementContainerUtil;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation.PerformanceDataFacilityViolation;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormValidator {

    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;

    public BusinessValidationResult validateReportSubmission(TargetPeriodDetailsDTO targetPeriod, PerformanceDataReportType reportType, LocalDate submissionDate) {
        List<PerformanceDataFacilityViolation> violations = new ArrayList<>();

        // Check if performance data eligible for Target Period
        if(submissionDate.isBefore(targetPeriod.getTargetPeriodYearsContainer().getTargetPeriodReportingStartDate())) {
            violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.TARGET_PERIOD_REPORTING_NOT_STARTED));
        }

        final Optional<LocalDate> reportingEndDate = targetPeriod.getTargetPeriodYearsContainer()
                .getTargetPeriodReportingEndDate();
        if(reportingEndDate.isPresent() && submissionDate.isAfter(reportingEndDate.get())) {
            violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.TARGET_PERIOD_REPORTING_IS_ENDED));
        }

        // Check if report type is eligible for Target period
        Optional<PerformanceDataReportType> validReportType = PerformanceDataFacilityUtil
                .getReportTypeBySubmissionDate(targetPeriod, submissionDate);
        if(validReportType.isEmpty() || !validReportType.get().equals(reportType)) {
            violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.REPORT_TYPE_NOT_VALID));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    public BusinessValidationResult validateFacilityEligibility(FacilityDTO facility, TargetPeriodDetailsDTO targetPeriod, LocalDate submissionDate) {
        final TargetPeriodYear targetPeriodYear = PerformanceDataFacilityUtil.getTargetPeriodYearBySubmissionDate(targetPeriod, submissionDate)
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));
        return validateFacilityEligibility(facility, targetPeriodYear);
    }

    public BusinessValidationResult validateFacilityEligibility(FacilityDTO facility, TargetPeriodYear targetPeriodYear) {
        final LocalDate facilityCreatedDate = facility.getCreatedDate().toLocalDate();

        List<PerformanceDataFacilityViolation> violations = new ArrayList<>();

        // IF active + [facility_created_date < REPORTING_period_start_date]
        // IF NOT_ACTIVE (facility_create_date < REPORTING_period_start_date AND REPORTING_period_start_date < facility_closed_date)
        if(!facilityCreatedDate.isBefore(targetPeriodYear.getReportingStartDate()) ||
                (facility.getClosedDate() != null && !facility.getClosedDate().isAfter(targetPeriodYear.getReportingStartDate()))) {
            violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    public BusinessValidationResult validateSecondaryReportingLock(TargetPeriodDetailsDTO targetPeriod, LocalDate submissionDate) {
        List<PerformanceDataFacilityViolation> violations = new ArrayList<>();

        // Check for locking if secondary report
        if(!submissionDate.isBefore(targetPeriod.getSecondaryReportingStartDate())) {
            // TODO LOCK Validation for secondary
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    public BusinessValidationResult validateFacilityProductsEligibility(FacilityDTO facility, TargetPeriodDetailsDTO targetPeriod, LocalDate submissionDate) {
        final TargetPeriodYear targetPeriodYear = PerformanceDataFacilityUtil.getTargetPeriodYearBySubmissionDate(targetPeriod, submissionDate)
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));
        final UnderlyingAgreementContainer una = underlyingAgreementQueryService
                .getUnderlyingAgreementContainerByAccountId(facility.getAccountId());
        final Cca3FacilityBaselineAndTargets baselineAndTargets = UnderlyingAgreementContainerUtil
                .getFacilityBaselineAndTargets(facility.getFacilityBusinessId(), una)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        final List<ProductVariableEnergyConsumptionData> products = baselineAndTargets.getFacilityBaselineEnergyConsumption()
                .getVariableEnergyConsumptionDataByProduct();

        List<PerformanceDataFacilityViolation> violations = new ArrayList<>();

        if(!products.isEmpty() && products.stream().allMatch(p -> p.getBaselineYear().getValue() > targetPeriodYear.getTargetYear().getValue())) {
            violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE_PRODUCTS));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
