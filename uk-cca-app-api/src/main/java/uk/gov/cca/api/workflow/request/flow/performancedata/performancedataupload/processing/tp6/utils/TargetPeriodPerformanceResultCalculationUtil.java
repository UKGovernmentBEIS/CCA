package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculationParameters;

import java.math.BigDecimal;
import java.util.Optional;

@UtilityClass
public class TargetPeriodPerformanceResultCalculationUtil {

    public Optional<BigDecimal> getTpPerformanceCalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal tpEnergy = ActualTargetPeriodPerformanceCalculationUtil.getTpEnergyCalculatedValue(parameters);

        return switch (parameters.getTargetType()) {
            case ABSOLUTE -> Optional.of(tpEnergy);

            case RELATIVE -> TargetPeriodPerformanceResultCalculationFunctionUtil.RELATIVE_TARGET_PERIOD_ENERGY
                    .apply(tpEnergy, parameters.getReportingThroughput());

            case NOVEM -> {
                BigDecimal tpCarbonFactor = PrimaryDeterminationCalculationUtil.getTpCarbonFactorCalculatedValue(parameters)
                        .orElse(null);

                yield TargetPeriodPerformanceResultCalculationFunctionUtil.NOVEM_TARGET_PERIOD_ENERGY
                    .apply(tpEnergy, parameters.getEnergyCarbonUnit(), tpCarbonFactor);
            }
        };
    }

    public Optional<BigDecimal> getTpPerformancePercentCalculatedValue(PerformanceDataCalculationParameters parameters) {
        return switch (parameters.getTargetType()) {
            case ABSOLUTE -> {
                BigDecimal multiplier = parameters.getType().equals(PerformanceDataTargetPeriodType.TP6)
                        ? BigDecimal.ONE : BigDecimal.TWO;
                BigDecimal tpPerformance = TargetPeriodPerformanceResultCalculationUtil.getTpPerformanceCalculatedValue(parameters)
                        .orElse(null);

                yield TargetPeriodPerformanceResultCalculationFunctionUtil.ABSOLUTE_TARGET_PERIOD_IMPROVEMENT_PERCENTAGE
                    .apply(tpPerformance, parameters.getByEnergyCarbon(), multiplier);
            }

            case RELATIVE -> {
                BigDecimal tpPerformance = TargetPeriodPerformanceResultCalculationUtil.getTpPerformanceCalculatedValue(parameters)
                        .orElse(null);
                BigDecimal byPerformance = TargetUnitDetailsCalculationUtil.getByPerformanceCalculatedValue(parameters)
                        .orElse(null);

                yield TargetPeriodPerformanceResultCalculationFunctionUtil.RELATIVE_TARGET_PERIOD_IMPROVEMENT_PERCENTAGE
                    .apply(tpPerformance, byPerformance);
            }

            case NOVEM -> {
                BigDecimal tpEnergy = ActualTargetPeriodPerformanceCalculationUtil.getTpEnergyCalculatedValue(parameters);
                BigDecimal tpCarbonFactor = PrimaryDeterminationCalculationUtil.getTpCarbonFactorCalculatedValue(parameters)
                        .orElse(null);

                yield TargetPeriodPerformanceResultCalculationFunctionUtil.NOVEM_TARGET_PERIOD_IMPROVEMENT_PERCENTAGE
                    .apply(tpEnergy, parameters.getEnergyCarbonUnit(), tpCarbonFactor, parameters.getByEnergyCarbonTpThroughput());
            }
        };
    }

    public TargetPeriodResultType getTpOutcomeCalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal priBuyOutCarbon = PrimaryDeterminationCalculationUtil.getPriBuyOutCarbonCalculatedValue(parameters)
                .orElse(null);
        BigDecimal surplusUsed = PrimaryDeterminationCalculationUtil.getSurplusUsedCalculatedValue(parameters)
                .orElse(null);
        BigDecimal surplusGained = PrimaryDeterminationCalculationUtil.getSurplusGainedCalculatedValue(parameters)
                .orElse(null);

        return TargetPeriodPerformanceResultCalculationFunctionUtil.TARGET_PERIOD_RESULT
                .apply(priBuyOutCarbon, surplusUsed, surplusGained);
    }
}
