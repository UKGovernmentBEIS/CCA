package uk.gov.cca.api.targetperiodreporting.performancedatafacility.util;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

@UtilityClass
public class PerformanceDataFacilityUtil {

    public Optional<PerformanceDataReportType> getReportTypeBySubmissionDate(final TargetPeriodDetailsDTO targetPeriod, final LocalDate submissionDate) {
        final TargetPeriodYear targetPeriodYear = getTargetPeriodYearBySubmissionDate(targetPeriod, submissionDate).orElse(null);
        final Year lastReportingYear = targetPeriod.getTargetPeriodYearsContainer().getFinalTargetPeriodTargetYear();

        if(targetPeriodYear == null || lastReportingYear == null ||
                submissionDate.isBefore(targetPeriod.getTargetPeriodYearsContainer().getTargetPeriodReportingStartDate())) {
            return Optional.empty();
        }

        return targetPeriodYear.getTargetYear().equals(lastReportingYear)
                ? Optional.of(PerformanceDataReportType.FINAL)
                : Optional.of(PerformanceDataReportType.INTERIM);
    }

    public Optional<TargetPeriodYear> getTargetPeriodYearBySubmissionDate(final TargetPeriodDetailsDTO targetPeriod, final LocalDate submissionDate) {
        return targetPeriod.getTargetPeriodYearsContainer().getTargetPeriodYears().stream()
                .filter(tpy -> !submissionDate.isBefore(tpy.getReportingStartDate()) &&
                        (tpy.getReportingEndDate() == null || !submissionDate.isAfter(tpy.getReportingEndDate())))
                .findFirst();
    }
}
