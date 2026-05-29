package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.util.PerformanceDataFacilityUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation.PerformanceDataFacilityViolation;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormValidator {

    private final PerformanceDataFacilityReferenceDataService performanceDataFacilityReferenceDataService;
    private final PerformanceDataFacilityStatusQueryService performanceDataFacilityStatusQueryService;

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

    public BusinessValidationResult validateFacilityBaselineDateEligibility(FacilityDTO facility, TargetPeriodDetailsDTO targetPeriod, LocalDate submissionDate) {
        final TargetPeriodYear targetPeriodYear = PerformanceDataFacilityUtil.getTargetPeriodYearBySubmissionDate(targetPeriod, submissionDate)
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));
        return validateFacilityBaselineDateEligibility(facility, targetPeriodYear);
    }

    public BusinessValidationResult validateFacilityBaselineDateEligibility(FacilityDTO facility, TargetPeriodYear targetPeriodYear) {
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = performanceDataFacilityReferenceDataService
                .getFacilityOriginalBaselineAndTargets(facility.getAccountId(), facility.getFacilityBusinessId(), targetPeriodYear.getTargetYear());

        List<PerformanceDataFacilityViolation> violations = new ArrayList<>();

        if(baselineAndTargets.getBaselineDate().isAfter(targetPeriodYear.getReportingStartDate())) {
            violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_BASELINE_DATE_NOT_ELIGIBLE));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    public BusinessValidationResult validateFacilityReportingLock(FacilityDTO facility, TargetPeriodDetailsDTO targetPeriod, LocalDate submissionDate) {
        final TargetPeriodYear targetPeriodYear = PerformanceDataFacilityUtil.getTargetPeriodYearBySubmissionDate(targetPeriod, submissionDate)
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));

        List<PerformanceDataFacilityViolation> violations = new ArrayList<>();

        // Check for locking
        if(performanceDataFacilityStatusQueryService.getLockedStatus(facility.getId(), targetPeriodYear.getTargetYear())) {
            violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_IS_LOCKED));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    public BusinessValidationResult validateFacilityProductsEligibility(FacilityDTO facility, TargetPeriodDetailsDTO targetPeriod, LocalDate submissionDate) {
        final TargetPeriodYear targetPeriodYear = PerformanceDataFacilityUtil.getTargetPeriodYearBySubmissionDate(targetPeriod, submissionDate)
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = performanceDataFacilityReferenceDataService
                .getFacilityOriginalBaselineAndTargets(facility.getAccountId(), facility.getFacilityBusinessId(), targetPeriodYear.getTargetYear());
        final List<ProductVariableEnergyConsumptionData> products = baselineAndTargets.getVariableEnergyConsumptionDataByProduct();

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
