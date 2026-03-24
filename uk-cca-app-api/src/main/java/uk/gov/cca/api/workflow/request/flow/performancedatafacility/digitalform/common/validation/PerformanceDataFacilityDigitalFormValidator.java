package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.util.PerformanceDataFacilityUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormValidator {

    private final TargetPeriodService targetPeriodService;
    private final FacilityDataQueryService facilityDataQueryService;

    public BusinessValidationResult validateReportSubmission(TargetPeriodType targetPeriodType, PerformanceDataReportType reportType, LocalDate submissionDate) {
        final TargetPeriodDetailsDTO targetPeriod = targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        List<PerformanceDataFacilityDigitalFormViolation> violations = new ArrayList<>();

        // Check if performance data eligible for Target Period
        if(submissionDate.isBefore(targetPeriod.getTargetPeriodYearsContainer().getTargetPeriodReportingStartDate())) {
            violations.add(new PerformanceDataFacilityDigitalFormViolation(PerformanceDataFacilityDigitalFormViolation
                    .PerformanceDataFacilityDigitalFormViolationMessage.TARGET_PERIOD_REPORTING_NOT_STARTED));
        }

        final Optional<LocalDate> reportingEndDate = targetPeriod.getTargetPeriodYearsContainer()
                .getTargetPeriodReportingEndDate();
        if(reportingEndDate.isPresent() && submissionDate.isAfter(reportingEndDate.get())) {
            violations.add(new PerformanceDataFacilityDigitalFormViolation(PerformanceDataFacilityDigitalFormViolation
                    .PerformanceDataFacilityDigitalFormViolationMessage.TARGET_PERIOD_REPORTING_IS_ENDED));
        }

        // Check if report type is eligible for Target period
        Optional<PerformanceDataReportType> validReportType = PerformanceDataFacilityUtil
                .getReportTypeBySubmissionDate(targetPeriod, submissionDate);
        if(validReportType.isEmpty() || !validReportType.get().equals(reportType)) {
            violations.add(new PerformanceDataFacilityDigitalFormViolation(PerformanceDataFacilityDigitalFormViolation
                    .PerformanceDataFacilityDigitalFormViolationMessage.REPORT_TYPE_NOT_VALID));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    public BusinessValidationResult validateFacilityEligibility(Long facilityId, TargetPeriodType targetPeriodType, LocalDate submissionDate) {
        final TargetPeriodDetailsDTO targetPeriod = targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        final FacilityDTO facility = facilityDataQueryService.getFacilityInfoData(facilityId);

        final TargetPeriodYear targetPeriodYear = PerformanceDataFacilityUtil.getTargetPeriodYearBySubmissionDate(targetPeriod, submissionDate)
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));
        final LocalDate facilityCreatedDate = facility.getCreatedDate().toLocalDate();
        List<PerformanceDataFacilityDigitalFormViolation> violations = new ArrayList<>();

        // IF active + [facility_created_date < REPORTING_period_start_date]
        // IF NOT_ACTIVE (facility_create_date < REPORTING_period_start_date AND REPORTING_period_start_date < facility_closed_date)
        if(!facilityCreatedDate.isBefore(targetPeriodYear.getReportingStartDate()) ||
                (facility.getClosedDate() != null && !facility.getClosedDate().isAfter(targetPeriodYear.getReportingStartDate()))) {
            violations.add(new PerformanceDataFacilityDigitalFormViolation(PerformanceDataFacilityDigitalFormViolation
                    .PerformanceDataFacilityDigitalFormViolationMessage.FACILITY_NOT_ELIGIBLE));
        }

        if(!submissionDate.isBefore(targetPeriod.getSecondaryReportingStartDate())) {
            // TODO LOCK Validation for secondary
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
