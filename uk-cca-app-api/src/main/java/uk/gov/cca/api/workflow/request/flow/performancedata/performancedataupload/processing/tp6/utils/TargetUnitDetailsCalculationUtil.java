package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculationParameters;

import java.math.BigDecimal;
import java.util.Optional;

@UtilityClass
public class TargetUnitDetailsCalculationUtil {

    public Optional<BigDecimal> getByPerformanceCalculatedValue(PerformanceDataCalculationParameters parameters) {
        return switch (parameters.getTargetType()) {
            case ABSOLUTE, RELATIVE -> TargetUnitDetailsCalculationFunctionUtil.BY_PERFORMANCE_CALCULATION
                    .apply(parameters.getByEnergyCarbon(), parameters.getByThroughput());

            case NOVEM -> Optional.empty();
        };
    }

    public Optional<BigDecimal> getNumericalTargetCalculatedValue(PerformanceDataCalculationParameters parameters) {
        return switch (parameters.getTargetType()) {
            case RELATIVE -> {
                BigDecimal byPerformance = TargetUnitDetailsCalculationUtil.getByPerformanceCalculatedValue(parameters)
                        .orElse(null);

                yield TargetUnitDetailsCalculationFunctionUtil.RELATIVE_NUMERICAL_TARGET
                        .apply(byPerformance, parameters.getPercentTarget());
            }

            case ABSOLUTE -> {
                // target_period="TP6",1,2
                BigDecimal multiplier = parameters.getType().equals(PerformanceDataTargetPeriodType.TP6)
                        ? BigDecimal.ONE : BigDecimal.TWO;

                yield TargetUnitDetailsCalculationFunctionUtil.ABSOLUTE_NUMERICAL_TARGET
                    .apply(parameters.getByEnergyCarbon(), parameters.getPercentTarget(), multiplier);
            }

            case NOVEM -> Optional.empty();
        };
    }

    public Optional<BigDecimal> getToleranceCalculatedValue(PerformanceDataCalculationParameters parameters) {
        return TargetUnitDetailsCalculationFunctionUtil.TOLERANCE.apply(parameters.getByEnergyCarbon(), parameters.getByThroughput(),
                parameters.getTolerancePercentage(), parameters.getPercentTarget());
    }
}
