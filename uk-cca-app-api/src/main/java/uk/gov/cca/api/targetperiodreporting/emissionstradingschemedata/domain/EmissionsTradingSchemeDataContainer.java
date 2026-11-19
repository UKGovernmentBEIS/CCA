package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFuel;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "#isUltraSmallEmitter == null || T(java.lang.Boolean).TRUE.equals(#isUltraSmallEmitter) " +
        "|| T(java.lang.Boolean).FALSE.equals(#isUltraSmallEmitter) == (#fuels?.size() gt 0)",
        message = "emissionstradingschemedata.energyFuelDetails.notEmpty")
public class EmissionsTradingSchemeDataContainer {

    @NotNull
    private MeasurementType measurementType;

    private String uketsId;

    @NotNull
    private Boolean isUltraSmallEmitter;

    @Builder.Default
    private List<@Valid @NotNull PerformanceDataFacilityFuel> fuels = new ArrayList<>();
}
