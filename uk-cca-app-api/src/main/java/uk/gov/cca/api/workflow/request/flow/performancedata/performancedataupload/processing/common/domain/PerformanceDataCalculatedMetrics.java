package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDataCalculatedMetrics implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Base year performance
    private BigDecimal byPerformance;

    // Numerical target
    private BigDecimal numericalTarget;

    // +/- Tolerance on target in target units
    private BigDecimal tolerance;

    // Target Period Total Energy
    private BigDecimal tpEnergy;

    // Target Period CHP delivered electricity
    private BigDecimal tpChpDeliveredElectricity;

    // Target Period Energy
    private BigDecimal tpPerformance;

    // Target Period Improvement Relative to Base Year %
    private BigDecimal tpPerformancePercent;

    // Target Period Result
    private TargetPeriodResultType tpOutcome;

    // Target period carbon factor
    private BigDecimal tpCarbonFactor;

    // Amount of energy used under target
    private BigDecimal energyCarbonUnderTarget;

    // Amount of CO2 emitted under target
    private BigDecimal carbonUnderTarget;

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

    // Secondary buy-out required
    private BigDecimal secondaryBuyOutCo2;

    // Secondary buy-out cost
    private BigDecimal secondaryBuyOutCost;
}
