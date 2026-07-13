package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityPerformanceDataReportDetailsDTO {

	// Details
	private TargetPeriodType targetPeriod;
	private boolean atLeastSeventyPercentEnergyUsed;
	// Calculated results
	private PerformanceDataFacilityCalculatedResults calculatedResults;
	// Baseline and targets
	private PerformanceDataFacilityBaselineAndTargets baselineAndTargets;
}
