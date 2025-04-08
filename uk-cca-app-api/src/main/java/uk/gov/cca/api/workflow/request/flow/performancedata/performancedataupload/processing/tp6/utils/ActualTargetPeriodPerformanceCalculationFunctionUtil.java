package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.common.domain.FourFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class ActualTargetPeriodPerformanceCalculationFunctionUtil {

    // tp_energy
    public final Function<List<BigDecimal>, BigDecimal> TOTAL_ENERGY = values ->
            values.stream().filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

    // IF(adjusted_throughput=0,0,IF(actual_throughput/adjusted_throughput*primary_electricity/2.6-primary_electricity/2.6<0,"Error",actual_throughput/adjusted_throughput*primary_electricity/2.6-primary_electricity/2.6))
    public final FourFunction<BigDecimal, BigDecimal, BigDecimal, BigDecimal, Optional<BigDecimal>> CHP_DELIVERED_ELECTRICITY =
            (actualThroughput, adjustedThroughput, primaryElectricity, tpDivider) -> {
        if(ObjectUtils.isEmpty(actualThroughput) || ObjectUtils.isEmpty(adjustedThroughput)
                || ObjectUtils.isEmpty(primaryElectricity) || adjustedThroughput.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.of(BigDecimal.ZERO);
        }

        BigDecimal group1 = tpDivider.compareTo(BigDecimal.ZERO) != 0
                ? actualThroughput.divide(adjustedThroughput, MathContext.DECIMAL128)
                    .multiply(primaryElectricity, MathContext.DECIMAL128).divide(tpDivider, MathContext.DECIMAL128)
                : BigDecimal.ZERO;

        BigDecimal group2 = tpDivider.compareTo(BigDecimal.ZERO) != 0
                ? primaryElectricity.divide(tpDivider, MathContext.DECIMAL128)
                : BigDecimal.ZERO;

        return Optional.of(group1.subtract(group2, MathContext.DECIMAL128));
    };
}
