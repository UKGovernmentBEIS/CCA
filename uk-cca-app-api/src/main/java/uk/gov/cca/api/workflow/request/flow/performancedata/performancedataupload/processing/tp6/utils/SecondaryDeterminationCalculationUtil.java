package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculationParameters;

import java.math.BigDecimal;
import java.util.Optional;

@UtilityClass
public class SecondaryDeterminationCalculationUtil {

    public Optional<BigDecimal> getSecondaryBuyOutCo2CalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal priBuyOutCarbon = PrimaryDeterminationCalculationUtil.getPriBuyOutCarbonCalculatedValue(parameters)
                .orElse(null);

        return SecondaryDeterminationCalculationFunctionUtil.SECONDARY_BUY_OUT_REQUIRED
                .apply(priBuyOutCarbon, parameters.getPrevSurplusUsed(), parameters.getPrevBuyOutCo2());
    }

    public Optional<BigDecimal> getSecondaryBuyOutCostCalculatedValue(PerformanceDataCalculationParameters parameters) {
        BigDecimal secondaryBuyOutCo2 = SecondaryDeterminationCalculationUtil.getSecondaryBuyOutCo2CalculatedValue(parameters)
                .orElse(null);
        BigDecimal multiplier = parameters.getType().getReferenceTargetPeriod().getCostPerCarbon();

        return SecondaryDeterminationCalculationFunctionUtil.SECONDARY_BUY_OUT_COST
                .apply(secondaryBuyOutCo2, multiplier);
    }
}
