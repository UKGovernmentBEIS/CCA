package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityReferenceData {
    private PerformanceDataFacilityBaselineAndTargets baselineAndTargets;
}
