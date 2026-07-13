package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityInputData {

    @Valid
    @NotNull
    private PerformanceDataFacilityInputEnergyFuelDetails energyFuelDetails;

    @Valid
    @NotNull
    private PerformanceDataFacilityThroughputDetails throughputDetails;

    @Valid
    private PerformanceDataFacilityCalculatedResults calculatedResults;
}
