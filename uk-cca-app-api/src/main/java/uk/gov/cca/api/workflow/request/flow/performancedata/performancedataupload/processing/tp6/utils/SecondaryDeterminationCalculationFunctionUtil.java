package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.common.domain.TriFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import java.util.function.BiFunction;

@UtilityClass
public class SecondaryDeterminationCalculationFunctionUtil {

    // MAX(pri_buy_out_carbon-prev_surplus_used,0)-prev_buy_out_co2
    public final TriFunction<BigDecimal, BigDecimal, BigDecimal, Optional<BigDecimal>> SECONDARY_BUY_OUT_REQUIRED =
            (priBuyOutCarbon, prevSurplusUsed, prevBuyOutCo2) -> {
        if(ObjectUtils.isEmpty(priBuyOutCarbon)) {
            return Optional.empty();
        }

        // pri_buy_out_carbon-prev_surplus_used
        BigDecimal group1 = Optional.ofNullable(prevSurplusUsed)
                .map(priBuyOutCarbon::subtract)
                .orElse(priBuyOutCarbon);

        // Set prev_buy_out_co2 to ZERO if not exist
        BigDecimal group2 = Optional.ofNullable(prevBuyOutCo2)
                .orElse(BigDecimal.ZERO);

        return group1.compareTo(BigDecimal.ZERO) > 0
                ? Optional.of(group1.subtract(group2, MathContext.DECIMAL128))
                : Optional.of(BigDecimal.ZERO.subtract(group2, MathContext.DECIMAL128));
    };

    // =ROUND(secondary_buy_out_co2*IFS(
    //target_period="TP6",25,
    //target_period="TP5",18,
    //OR(target_period="TP3",target_period="TP4"),14,
    //OR(target_period="TP1",target_period="TP2"),12),0)
    public final BiFunction<BigDecimal, BigDecimal, Optional<BigDecimal>> SECONDARY_BUY_OUT_COST =
            (secondaryBuyOutCo2, multiplier) -> {
        if(ObjectUtils.isEmpty(secondaryBuyOutCo2)) {
            return Optional.empty();
        }

        return Optional.of(secondaryBuyOutCo2.multiply(multiplier, MathContext.UNLIMITED));
    };
}
