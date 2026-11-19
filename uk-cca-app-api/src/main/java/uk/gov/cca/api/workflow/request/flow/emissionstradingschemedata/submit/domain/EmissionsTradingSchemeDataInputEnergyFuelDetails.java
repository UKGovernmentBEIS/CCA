package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceEnergyFuelDetails;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "!#standardFuels?.containsKey('GRID_ELECTRICITY') " +
        "&& !#standardFuels?.containsKey('NON_GRID_ELECTRICITY')  " +
        "&& !#standardFuels?.containsKey('NITROGEN_COOLING') " +
        "&& !#standardFuels?.containsKey('CARBON_DIOXIDE_COOLING')",
        message = "emissionstradingschemedata.standardFuels.invalid")
@SpELExpression(expression = "#standardFuels.size() gt 0 || #nonStandardFuels.size() gt 0",
        message = "emissionstradingschemedata.energyFuelDetails.notEmpty")
public class EmissionsTradingSchemeDataInputEnergyFuelDetails implements PerformanceEnergyFuelDetails {

    @Builder.Default
    private Map<PerformanceDataFacilityFixedConversionFactor, @Valid @NotNull PerformanceDataFacilityFuelEnergyConsumption> standardFuels = new EnumMap<>(PerformanceDataFacilityFixedConversionFactor.class);

    @Size(max = 10)
    @Builder.Default
    private List<@Valid @NotNull PerformanceDataFacilityNonStandardFuel> nonStandardFuels = new ArrayList<>();

    @Valid
    @NotNull
    @JsonUnwrapped
    private EmissionsTradingSchemeDataCalculatedResults calculatedResults;
}
