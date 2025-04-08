package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculationParameters;

import java.math.BigDecimal;
import java.util.Optional;

@UtilityClass
public class PrimaryDeterminationCalculationUtil {

    public Optional<BigDecimal> getTpCarbonFactorCalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal tpEnergy = ActualTargetPeriodPerformanceCalculationUtil.getTpEnergyCalculatedValue(parameters);

        return PrimaryDeterminationCalculationFunctionUtil.TP6_TARGET_PERIOD_CARBON_FACTOR
                .apply(parameters.getEnergyData(), parameters.getCarbonFactors(), tpEnergy, parameters.getEnergyCarbonUnit());
    }

    public Optional<BigDecimal> getEnergyCarbonUnderTargetCalculatedValue(PerformanceDataCalculationParameters parameters) {
        return switch (parameters.getTargetType()) {
            case ABSOLUTE -> {
                BigDecimal numericalTarget = TargetUnitDetailsCalculationUtil.getNumericalTargetCalculatedValue(parameters)
                        .orElse(null);
                BigDecimal tpPerformance = TargetPeriodPerformanceResultCalculationUtil.getTpPerformanceCalculatedValue(parameters)
                        .orElse(null);

                yield PrimaryDeterminationCalculationFunctionUtil.ABSOLUTE_AMOUNT_ENERGY_USED_UNDER_TARGET
                    .apply(numericalTarget, tpPerformance);
            }
            case RELATIVE -> {
                BigDecimal numericalTarget = TargetUnitDetailsCalculationUtil.getNumericalTargetCalculatedValue(parameters)
                        .orElse(null);
                BigDecimal tpPerformance = TargetPeriodPerformanceResultCalculationUtil.getTpPerformanceCalculatedValue(parameters)
                        .orElse(null);
                BigDecimal tolerance = TargetUnitDetailsCalculationUtil.getToleranceCalculatedValue(parameters)
                        .orElse(null);
                BigDecimal tpPerformancePercent = TargetPeriodPerformanceResultCalculationUtil.getTpPerformancePercentCalculatedValue(parameters)
                        .orElse(null);

                BigDecimal group2 = PrimaryDeterminationCalculationFunctionUtil.RELATIVE_GROUP2_AMOUNT_ENERGY_USED_UNDER_TARGET
                        .apply(tolerance, tpPerformancePercent, parameters.getPercentTarget())
                        .orElse(null);

                yield PrimaryDeterminationCalculationFunctionUtil.RELATIVE_AMOUNT_ENERGY_USED_UNDER_TARGET
                        .apply(numericalTarget, group2, tpPerformance, parameters.getReportingThroughput());
            }
            case NOVEM -> {
                BigDecimal tpEnergy = ActualTargetPeriodPerformanceCalculationUtil.getTpEnergyCalculatedValue(parameters);
                BigDecimal tpCarbonFactor = PrimaryDeterminationCalculationUtil.getTpCarbonFactorCalculatedValue(parameters)
                        .orElse(null);

                yield PrimaryDeterminationCalculationFunctionUtil.NOVEM_AMOUNT_ENERGY_USED_UNDER_TARGET
                    .apply(parameters.getTargetEnergyCarbonTpThroughput(), tpEnergy, tpCarbonFactor, parameters.getEnergyCarbonUnit());
            }
        };
    }

    public Optional<BigDecimal> getCarbonUnderTargetCalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal energyCarbonUnderTarget = PrimaryDeterminationCalculationUtil.getEnergyCarbonUnderTargetCalculatedValue(parameters)
                .orElse(null);
        BigDecimal tpCarbonFactor = getTpCarbonFactorCalculatedValue(parameters)
                .orElse(null);

        return PrimaryDeterminationCalculationFunctionUtil.AMOUNT_CO2_EMITTED_UNDER_TARGET
                .apply(energyCarbonUnderTarget, tpCarbonFactor, parameters.getEnergyCarbonUnit());
    }

    public Optional<BigDecimal> getCo2EmissionsCalculatedValue(PerformanceDataCalculationParameters parameters) {
        return PrimaryDeterminationCalculationFunctionUtil.CARBON_DIOXIDE_EMITTED
                .apply(parameters.getEnergyData(), parameters.getCarbonFactors(), parameters.getEnergyCarbonUnit(), parameters.getType());
    }

    public Optional<BigDecimal> getSurplusUsedCalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal tpPerformancePercent = TargetPeriodPerformanceResultCalculationUtil.getTpPerformancePercentCalculatedValue(parameters)
                .orElse(null);
        BigDecimal carbonUnderTarget = getCarbonUnderTargetCalculatedValue(parameters)
                .orElse(null);
        BigDecimal percentTarget = parameters.getTargetType().equals(AgreementCompositionType.NOVEM)
                ? CommonCalculationFunctionUtil.NOVEM_PERCENT_TARGET_GROUP_SURPLUS
                    .apply(parameters.getTargetEnergyCarbonTpThroughput(), parameters.getByEnergyCarbonTpThroughput())
                    .orElse(null)
                : parameters.getPercentTarget();

        return parameters.getType().equals(PerformanceDataTargetPeriodType.TP6)
                ? PrimaryDeterminationCalculationFunctionUtil.TP6_BANKED_SURPLUS_USED
                    .apply(tpPerformancePercent, percentTarget, parameters.getBankedSurplus(), carbonUnderTarget)
                : Optional.empty();
    }

    public Optional<BigDecimal> getSurplusGainedCalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal tpPerformancePercent = TargetPeriodPerformanceResultCalculationUtil.getTpPerformancePercentCalculatedValue(parameters)
                .orElse(null);
        BigDecimal carbonUnderTarget = getCarbonUnderTargetCalculatedValue(parameters)
                .orElse(null);
        BigDecimal percentTarget = parameters.getTargetType().equals(AgreementCompositionType.NOVEM)
                ? CommonCalculationFunctionUtil.NOVEM_PERCENT_TARGET_GROUP_SURPLUS
                    .apply(parameters.getTargetEnergyCarbonTpThroughput(), parameters.getByEnergyCarbonTpThroughput())
                    .orElse(null)
                : parameters.getPercentTarget();

        return PrimaryDeterminationCalculationFunctionUtil.SURPLUS_GAINED
                .apply(tpPerformancePercent, percentTarget, carbonUnderTarget);
    }

    public Optional<BigDecimal> getPriBuyOutCarbonCalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal energyCarbonUnderTarget = PrimaryDeterminationCalculationUtil.getEnergyCarbonUnderTargetCalculatedValue(parameters)
                .orElse(null);
        BigDecimal tpCarbonFactor = getTpCarbonFactorCalculatedValue(parameters)
                .orElse(null);

        return parameters.getType().equals(PerformanceDataTargetPeriodType.TP6)
                ? PrimaryDeterminationCalculationFunctionUtil.TP6_BUY_OUT_REQUIRED
                    .apply(parameters.getBankedSurplus(), energyCarbonUnderTarget, tpCarbonFactor, parameters.getEnergyCarbonUnit())
                : Optional.empty();
    }

    public Optional<BigDecimal> getPriBuyOutCostCalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal priBuyOutCarbon = PrimaryDeterminationCalculationUtil.getPriBuyOutCarbonCalculatedValue(parameters)
                .orElse(null);
        BigDecimal multiplier = parameters.getType().equals(PerformanceDataTargetPeriodType.TP6)
                ? BigDecimal.valueOf(25) : BigDecimal.ZERO;

        return PrimaryDeterminationCalculationFunctionUtil.BUY_OUT_COST
                .apply(priBuyOutCarbon, multiplier);
    }
}
