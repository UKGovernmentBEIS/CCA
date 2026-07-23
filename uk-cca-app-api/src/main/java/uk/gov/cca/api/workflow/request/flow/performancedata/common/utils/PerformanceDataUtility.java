package uk.gov.cca.api.workflow.request.flow.performancedata.common.utils;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodYearDTO;

import java.time.LocalDate;

@UtilityClass
public class PerformanceDataUtility {

    public static PerformanceDataSubmissionType determinePerformanceDataSubmissionType(LocalDate reportDate, TargetPeriodYearDTO targetPeriodDTO) {
        LocalDate cutoffDate = targetPeriodDTO.getSecondaryReportingStartDate();
        return reportDate.isBefore(cutoffDate) ? PerformanceDataSubmissionType.PRIMARY
                : PerformanceDataSubmissionType.SECONDARY;
    }
}
