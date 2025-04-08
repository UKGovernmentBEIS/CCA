package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDataCalculationParameters {

    private PerformanceDataTargetPeriodType type;

    // Energy/carbon unit
    private AgreementCompositionType targetType;

    // Energy/carbon unit
    private MeasurementType energyCarbonUnit;

    // Base year energy
    private BigDecimal byEnergyCarbon;

    // Base year throughput
    private BigDecimal byThroughput;

    // Percent improvement target
    private BigDecimal percentTarget;

    // +/- Tolerance on target %
    private BigDecimal tolerancePercentage;

    // Banked surplus from previous Target Period (tCO2e)
    private BigDecimal bankedSurplus;

    // Throughput
    private BigDecimal actualThroughput;

    private Map<FixedConversionFactor, BigDecimal> energyData;

    private List<OtherFuel> carbonFactors;

    // Adjusted target period throughput
    private BigDecimal reportingThroughput;

    // Adjusted target period throughput of Target Unit Entry
    private BigDecimal adjustedThroughput;

    // Target energy at target period throughput
    private BigDecimal targetEnergyCarbonTpThroughput;

    // Base year energy at target period throughput
    private BigDecimal byEnergyCarbonTpThroughput;

    // Previous buy-out required after use of surplus
    private BigDecimal prevBuyOutCo2;

    // Previous surplus used
    private BigDecimal prevSurplusUsed;

    // Surplus gained in TP
    private BigDecimal prevSurplusGained;
}
