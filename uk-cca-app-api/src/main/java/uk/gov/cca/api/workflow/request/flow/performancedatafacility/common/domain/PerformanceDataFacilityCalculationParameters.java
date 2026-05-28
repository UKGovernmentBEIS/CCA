package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataFacilityCalculationParameters extends PerformanceDataFacilityBaselineAndTargets {
    private TargetPeriodType targetPeriodType;
    private Year targetYear;
    private PerformanceDataReportType reportType;
    private BigDecimal tpMultiplier;
    private BigDecimal targetImprovement;
    private Map<TargetPeriodType, Integer> lastYearPerTp;
}
