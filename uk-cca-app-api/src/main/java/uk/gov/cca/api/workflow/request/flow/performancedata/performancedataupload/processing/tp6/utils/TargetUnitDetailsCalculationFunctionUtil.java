package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.common.domain.FourFunction;
import uk.gov.cca.api.common.domain.TriFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import java.util.function.BiFunction;

@UtilityClass
public class TargetUnitDetailsCalculationFunctionUtil {

    // IF(by_throughput=0,0,by_energy_carbon/by_throughput)
    public final BiFunction<BigDecimal, BigDecimal, Optional<BigDecimal>> BY_PERFORMANCE_CALCULATION =
            (byEnergyCarbon, byThroughput) -> {
        if(ObjectUtils.isEmpty(byEnergyCarbon) || ObjectUtils.isEmpty(byThroughput)) {
            return Optional.of(BigDecimal.ZERO);
        }
        return byThroughput.compareTo(BigDecimal.ZERO) != 0
                ? Optional.of(byEnergyCarbon.divide(byThroughput, MathContext.DECIMAL128))
                : Optional.of(BigDecimal.ZERO);
    };

    // by_performance * (1-percent_target)
    public final BiFunction<BigDecimal, BigDecimal, Optional<BigDecimal>> RELATIVE_NUMERICAL_TARGET =
            (byPerformance, percentTarget) -> {
        if(ObjectUtils.isEmpty(byPerformance) || ObjectUtils.isEmpty(percentTarget)) {
            return Optional.empty();
        }
        return Optional.of((BigDecimal.ONE.subtract(percentTarget)).multiply(byPerformance, MathContext.DECIMAL128));
    };

    // by_energy_carbon * IF(target_period="TP6",1,2) * (1-percent_target)
    public final TriFunction<BigDecimal, BigDecimal, BigDecimal, Optional<BigDecimal>> ABSOLUTE_NUMERICAL_TARGET =
            (byEnergyCarbon, percentTarget, multiplier) -> {
        if(ObjectUtils.isEmpty(byEnergyCarbon) || ObjectUtils.isEmpty(percentTarget) || ObjectUtils.isEmpty(multiplier)) {
            return Optional.empty();
        }

        return Optional.of((BigDecimal.ONE.subtract(percentTarget))
                .multiply(multiplier, MathContext.DECIMAL128)
                .multiply(byEnergyCarbon, MathContext.DECIMAL128));
    };

    // by_energy_carbon / by_throughput / (1-percent_tolerance / (1-percent_target) ) - by_energy_carbon / by_throughput
    public final FourFunction<BigDecimal, BigDecimal, BigDecimal, BigDecimal, Optional<BigDecimal>> TOLERANCE =
            (byEnergyCarbon, byThroughput, percentTolerance, percentTarget) -> {
        if(ObjectUtils.isEmpty(byEnergyCarbon) || ObjectUtils.isEmpty(byThroughput) || ObjectUtils.isEmpty(percentTarget)) {
            return Optional.empty();
        }

        if(byThroughput.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.of(BigDecimal.ZERO);
        }

        // (1-percent_target)
        BigDecimal group4 = BigDecimal.ONE.subtract(percentTarget);
        // percent_tolerance / (1-percent_target)
        BigDecimal group3 = group4.compareTo(BigDecimal.ZERO) != 0
                ? Optional.ofNullable(percentTolerance).orElse(BigDecimal.ZERO).divide(group4, MathContext.DECIMAL128)
                : BigDecimal.ZERO;
        // (1-percent_tolerance / (1-percent_target) )
        BigDecimal group2 = BigDecimal.ONE.subtract(group3);
        // by_energy_carbon / by_throughput / (1-percent_tolerance / (1-percent_target) )
        BigDecimal group1 = group2.compareTo(BigDecimal.ZERO) != 0
                ? byEnergyCarbon.divide(byThroughput, MathContext.DECIMAL128).divide(group2, MathContext.DECIMAL128)
                : BigDecimal.ZERO;
        // by_energy_carbon / by_throughput
        BigDecimal group5 = byEnergyCarbon.divide(byThroughput, MathContext.DECIMAL128);

        return Optional.of(group1.subtract(group5, MathContext.DECIMAL128));
    };
}
