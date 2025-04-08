package uk.gov.cca.api.targetperiodreporting.performancedata.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
public class ActualPerformance {
    
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal actualThroughput;

    @Builder.Default
    List<FuelUsed> fuelsUsed = new ArrayList<>();

    // Target Period Total Energy
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal tpEnergy;

    /** Section 3a: Only for Target Units using the Special Reporting Method */

    // Target Period CHP delivered electricity (for Novem)
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal tpChpDeliveredElectricity;
    
    // Reporting target period throughput of Target Unit Entry
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal reportingThroughput;
}
