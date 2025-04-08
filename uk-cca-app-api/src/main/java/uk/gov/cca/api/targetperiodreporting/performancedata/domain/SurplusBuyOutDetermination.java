package uk.gov.cca.api.targetperiodreporting.performancedata.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurplusBuyOutDetermination {

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

    // Banked surplus used
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal surplusUsed;

    // Surplus gained
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal surplusGained;

    // Buy-out required
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal priBuyOutCarbon;

    // Buy-out cost
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal priBuyOutCost;

    // Total target period buy-out required
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal totalPriBuyOutCarbon;

    // Secondary buy-out required
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal secondaryBuyOutCo2;

    // Secondary buy-out cost
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal secondaryBuyOutCost;

    // Previous buy-out required after use of surplus
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal prevBuyOutCo2;

    // Previous surplus used
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal prevSurplusUsed;

    // Surplus gained in TP
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal prevSurplusGained;
}
