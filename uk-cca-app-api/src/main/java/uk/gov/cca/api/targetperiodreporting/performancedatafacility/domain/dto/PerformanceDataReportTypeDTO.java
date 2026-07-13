package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PerformanceDataReportTypeDTO {
    private TargetPeriodType targetPeriodType;
    private PerformanceDataReportType reportType;
}
