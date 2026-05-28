package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityReferenceData {
    private PerformanceDataFacilityBaselineAndTargets baselineAndTargets;
    private BigDecimal tpMultiplier;
}
