package uk.gov.cca.api.workflow.request.flow.performancedata.common.utils;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;

import java.time.LocalDate;

@UtilityClass
public class PerformanceDataUtility {

    public static PerformanceDataSubmissionType determinePerformanceDataSubmissionType(LocalDate reportDate, TargetPeriodDTO targetPeriodDTO) {
        LocalDate cutoffDate = targetPeriodDTO.getSecondaryReportingStartDate();
        return reportDate.isBefore(cutoffDate) ? PerformanceDataSubmissionType.PRIMARY
                : PerformanceDataSubmissionType.SECONDARY;
    }
}
