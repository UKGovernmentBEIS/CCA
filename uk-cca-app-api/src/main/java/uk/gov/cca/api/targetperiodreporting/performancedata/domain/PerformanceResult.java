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
public class PerformanceResult {

    // Target energy at target period throughput
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal targetEnergyCarbonTpThroughput;

    // Base year energy at target period throughput
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal byEnergyCarbonTpThroughput;

    // Target Period Energy
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal tpPerformance;

    // Target Period Improvement Relative to Base Year %
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal tpPerformancePercent;

    // Target Period Result
    @NotNull
    private TargetPeriodResultType tpOutcome;

}
