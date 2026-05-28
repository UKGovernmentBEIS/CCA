package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityFuelEnergyConsumption {

    @NotNull
    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal deliveredEnergy;

    @NotNull
    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal primaryEnergy;
}
