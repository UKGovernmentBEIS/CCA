package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondaryDetermination implements TP6PerformanceDataSection {

    // Target period carbon factor
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal tpCarbonFactor;

    // Amount of energy used under target
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal energyCarbonUnderTarget;

    // Amount of CO2 emitted under target
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal carbonUnderTarget;

    // Carbon dioxide emitted
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal co2Emissions;

    // Buy-out required
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal priBuyOutCarbon;

    // Previous buy-out required after use of surplus
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal prevBuyOutCo2;

    // Previous surplus used
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal prevSurplusUsed;

    // Surplus gained in TP
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal prevSurplusGained;

    // Secondary buy-out required
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal secondaryBuyOutCo2;

    // Secondary buy-out cost
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal secondaryBuyOutCost;
}
