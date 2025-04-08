package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.OtherFuel;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.FixedConversionFactor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ActualTargetPeriodPerformanceCalculationUtil {

    public BigDecimal getTpEnergyCalculatedValue(PerformanceDataCalculationParameters parameters) {
        List<BigDecimal> values = new ArrayList<>(parameters.getEnergyData().values());
        List<BigDecimal> customValues = parameters.getCarbonFactors().stream()
                .map(OtherFuel::getConsumption).toList();
        values.addAll(customValues);

        return ActualTargetPeriodPerformanceCalculationFunctionUtil.TOTAL_ENERGY.apply(values);
    }

    public BigDecimal getTpChpDeliveredElectricityCalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal primaryElectricity = parameters.getEnergyData().getOrDefault(FixedConversionFactor.ELECTRICITY, BigDecimal.ZERO);

        BigDecimal calculationResult = ActualTargetPeriodPerformanceCalculationFunctionUtil.CHP_DELIVERED_ELECTRICITY
                .apply(parameters.getActualThroughput(), parameters.getAdjustedThroughput(), primaryElectricity, BigDecimal.valueOf(2.6))
                .orElse(BigDecimal.ZERO);

        // =IF(adjusted_throughput=0,0,IF(actual_throughput/adjusted_throughput*primary_electricity/2.6-primary_electricity/2.6<0,"Error",actual_throughput/adjusted_throughput*primary_electricity/2.6-primary_electricity/2.6))
        return calculationResult.compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO
                : calculationResult;
    }
}
