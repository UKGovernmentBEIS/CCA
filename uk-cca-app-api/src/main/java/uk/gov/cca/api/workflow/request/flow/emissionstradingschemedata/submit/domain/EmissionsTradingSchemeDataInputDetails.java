package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "#isUltraSmallEmitter == null " +
        "|| (T(java.lang.Boolean).TRUE.equals(#isUltraSmallEmitter) && #provideFuelDetails != null) " +
        "|| (T(java.lang.Boolean).FALSE.equals(#isUltraSmallEmitter) && #provideFuelDetails == null) ",
        message = "emissionstradingschemedata.provideFuelDetails.invalid")
@SpELExpression(expression = "#isUltraSmallEmitter == null " +
        "|| (T(java.lang.Boolean).FALSE.equals(#isUltraSmallEmitter) && #energyFuelDetails != null) " +
        "|| (#provideFuelDetails != null && (T(java.lang.Boolean).TRUE.equals(#provideFuelDetails) && #energyFuelDetails != null)) " +
        "|| (#provideFuelDetails != null && (T(java.lang.Boolean).FALSE.equals(#provideFuelDetails) && #energyFuelDetails == null))",
        message = "emissionstradingschemedata.energyFuelDetails.invalid")
public class EmissionsTradingSchemeDataInputDetails {

    @NotNull
    private Boolean isUltraSmallEmitter;

    private Boolean provideFuelDetails;

    @Valid
    private EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails;
}
