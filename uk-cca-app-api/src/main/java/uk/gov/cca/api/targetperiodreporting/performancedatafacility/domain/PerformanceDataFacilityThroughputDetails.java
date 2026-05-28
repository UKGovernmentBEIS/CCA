package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityThroughputDetails {

    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal actualThroughput;

    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal targetImprovement;

    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal adjustedThroughput;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal totalTargetVariableEnergy;

    @Valid
    @Builder.Default
    private List<PerformanceDataFacilityProductVariableEnergyData> variableEnergyConsumptionDataByProduct = new ArrayList<>();
}
