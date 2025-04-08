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
public class CarbonSurplusBuyOutDTO {

    // Carbon dioxide emitted
    private BigDecimal co2Emissions;

    // Banked surplus used
    private BigDecimal surplusUsed;

    // Surplus gained
    private BigDecimal surplusGained;

    // Buy-out required
    private BigDecimal priBuyOutCarbon;

    // Buy-out cost
    private BigDecimal priBuyOutCost;
}
