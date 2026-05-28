package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityCalculatedResults {

    // Actual target period energy or carbon
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal actualEnergyCarbon;

    // Target energy/CO2
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal targetEnergyCarbon;

    // Energy/carbon difference
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal energyCarbonDifference;

    // Improvement target
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 9)
    private BigDecimal targetImprovement;

    // Target period weighted conversion factor
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal weightedConversionFactor;

    // Target tCO2e emitted
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal targetCo2Emissions;

    // Actual tCO2e emitted
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal actualCo2Emissions;

    // tCO2e difference
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal co2EmissionsDifference;

    // Improvement % achieved
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 9)
    private BigDecimal actualImprovement;

    // Target period result
    private PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType;

    // Total surplus gained
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal surplusGained;

    // Total buy-out required
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal buyOutRequired;
}
