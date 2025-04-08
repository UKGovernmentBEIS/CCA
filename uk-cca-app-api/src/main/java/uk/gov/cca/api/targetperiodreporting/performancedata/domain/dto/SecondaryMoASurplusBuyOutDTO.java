package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondaryMoASurplusBuyOutDTO {

    // Carbon dioxide emitted
    private BigDecimal co2Emissions;

    // Total target period Buy-out required (tCO2e)
    private BigDecimal priBuyOutCarbon;

    // Previous buy-out required after use of surplus
    private BigDecimal prevBuyOutCo2;

    // Previous surplus used
    private BigDecimal prevSurplusUsed;

    // Surplus gained in TP
    private BigDecimal prevSurplusGained;

    // Secondary buy-out required
    private BigDecimal secondaryBuyOutCo2;

    // Secondary buy-out cost or refund
    private BigDecimal secondaryBuyOutCost;

}
