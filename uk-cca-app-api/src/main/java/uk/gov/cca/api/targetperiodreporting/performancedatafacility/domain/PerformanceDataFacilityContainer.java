package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityContainer {

    @NotNull
    private PerformanceDataFacilityBaselineAndTargets baselineAndTargets;

    @Valid
    @NotNull
    private PerformanceDataFacilityEnergyFuelDetails energyFuelDetails;

    @Valid
    @NotNull
    private PerformanceDataFacilityThroughputDetails throughputDetails;

    @Valid
    @NotNull
    private PerformanceDataFacilityCalculatedResults calculatedResults;
}
