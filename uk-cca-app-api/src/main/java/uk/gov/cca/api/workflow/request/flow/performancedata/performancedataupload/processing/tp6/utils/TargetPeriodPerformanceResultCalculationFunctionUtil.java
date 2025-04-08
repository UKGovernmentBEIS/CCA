package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.common.domain.FourFunction;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.TriFunction;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import java.util.function.BiFunction;

@UtilityClass
public class TargetPeriodPerformanceResultCalculationFunctionUtil {

    // IF(reporting_throughput=0,0,tp_energy/reporting_throughput)
    public final BiFunction<BigDecimal, BigDecimal, Optional<BigDecimal>> RELATIVE_TARGET_PERIOD_ENERGY = (tpEnergy, reportingThroughput) -> {
        if(ObjectUtils.isEmpty(tpEnergy) || ObjectUtils.isEmpty(reportingThroughput)) {
            return Optional.empty();
        }

        return reportingThroughput.compareTo(BigDecimal.ZERO) != 0
                ? Optional.of(tpEnergy.divide(reportingThroughput, MathContext.DECIMAL128))
                : Optional.of(BigDecimal.ZERO);
    };

    // tp_energy * IF(energy_carbon_unit="kg", tp_carbon_factor, IF(energy_carbon_unit="tonne", tp_carbon_factor/1000,1))
    public final TriFunction<BigDecimal, MeasurementType, BigDecimal, Optional<BigDecimal>> NOVEM_TARGET_PERIOD_ENERGY =
            (tpEnergy, energyCarbonUnit, tpCarbonFactor) -> {
        if(ObjectUtils.isEmpty(tpEnergy) || ObjectUtils.isEmpty(energyCarbonUnit) || ObjectUtils.isEmpty(tpCarbonFactor)) {
            return Optional.of(BigDecimal.ZERO);
        }

        return Optional.of(CommonCalculationFunctionUtil.ENERGY_CARBON
                .apply(tpEnergy, tpCarbonFactor, energyCarbonUnit));
    };

    // IF(by_energy_carbon=0,0,1-(tp_performance/(by_energy_carbon*IF(target_period="TP6",1,2))))
    public final TriFunction<BigDecimal, BigDecimal, BigDecimal, Optional<BigDecimal>> ABSOLUTE_TARGET_PERIOD_IMPROVEMENT_PERCENTAGE =
            (tpPerformance, byEnergyCarbon, multiplier) -> {
        if(ObjectUtils.isEmpty(tpPerformance) || ObjectUtils.isEmpty(byEnergyCarbon) || ObjectUtils.isEmpty(multiplier)) {
            return Optional.empty();
        }

        BigDecimal group2 = byEnergyCarbon.compareTo(BigDecimal.ZERO) != 0
                ? tpPerformance.divide(byEnergyCarbon, MathContext.DECIMAL128).multiply(multiplier, MathContext.DECIMAL128)
                : BigDecimal.ONE;

        return Optional.of(BigDecimal.ONE.subtract(group2, MathContext.DECIMAL128));
    };

    // IF(by_performance=0,0,1-(tp_performance/by_performance))
    public final BiFunction<BigDecimal, BigDecimal, Optional<BigDecimal>> RELATIVE_TARGET_PERIOD_IMPROVEMENT_PERCENTAGE =
            (tpPerformance, byPerformance) -> {
        if(ObjectUtils.isEmpty(tpPerformance) || ObjectUtils.isEmpty(byPerformance)) {
            return Optional.empty();
        }

        BigDecimal group2 = byPerformance.compareTo(BigDecimal.ZERO) != 0
                ? tpPerformance.divide(byPerformance, MathContext.DECIMAL128)
                : BigDecimal.ONE;

        return Optional.of(BigDecimal.ONE.subtract(group2, MathContext.DECIMAL128));
    };

    // 1-(tp_energy*IF(energy_carbon_unit="kg",tp_carbon_factor,IF(energy_carbon_unit="tonne",tp_carbon_factor/1000,1))/IF(by_energy_carbon_tp_throughput=0,1,by_energy_carbon_tp_throughput))
    public final FourFunction<BigDecimal, MeasurementType, BigDecimal, BigDecimal, Optional<BigDecimal>> NOVEM_TARGET_PERIOD_IMPROVEMENT_PERCENTAGE =
            (tpEnergy, energyCarbonUnit, tpCarbonFactor, byEnergyCarbonTpThroughput) -> {
        if (ObjectUtils.isEmpty(tpEnergy) || ObjectUtils.isEmpty(energyCarbonUnit)
                || ObjectUtils.isEmpty(tpCarbonFactor) || ObjectUtils.isEmpty(byEnergyCarbonTpThroughput)) {
            return Optional.empty();
        }

        BigDecimal group2 = CommonCalculationFunctionUtil.ENERGY_CARBON.apply(tpEnergy, tpCarbonFactor, energyCarbonUnit);

        BigDecimal group3 = byEnergyCarbonTpThroughput.compareTo(BigDecimal.ZERO) != 0
                ? group2.divide(byEnergyCarbonTpThroughput, MathContext.DECIMAL128)
                : group2.divide(BigDecimal.ONE, MathContext.DECIMAL128);

        return Optional.of(BigDecimal.ONE.subtract(group3, MathContext.DECIMAL128));
    };

    // AND(pri_buy_out_carbon=0,surplus_used=0,surplus_gained>=0),"Target met",
    // AND(pri_buy_out_carbon>0,surplus_used=0),"Buy-out Required",
    // AND(pri_buy_out_carbon>0,surplus_used>0),"Surplus used and buy-out required",
    // AND(pri_buy_out_carbon=0,surplus_used>0),"Surplus used"
    public final TriFunction<BigDecimal, BigDecimal, BigDecimal, TargetPeriodResultType> TARGET_PERIOD_RESULT =
            (priBuyOutCarbon, surplusUsed, surplusGained) -> {
        if(ObjectUtils.isEmpty(priBuyOutCarbon) || ObjectUtils.isEmpty(surplusUsed)) {
            return null;
        }

        if(priBuyOutCarbon.compareTo(BigDecimal.ZERO) == 0 && surplusUsed.compareTo(BigDecimal.ZERO) == 0 && surplusGained.compareTo(BigDecimal.ZERO) >= 0) {
            return TargetPeriodResultType.TARGET_MET;
        } else if(priBuyOutCarbon.compareTo(BigDecimal.ZERO) > 0 && surplusUsed.compareTo(BigDecimal.ZERO) == 0) {
            return TargetPeriodResultType.BUY_OUT_REQUIRED;
        } else if(priBuyOutCarbon.compareTo(BigDecimal.ZERO) > 0 && surplusUsed.compareTo(BigDecimal.ZERO) > 0) {
            return TargetPeriodResultType.SURPLUS_USED_BUY_OUT_REQUIRED;
        } else if(priBuyOutCarbon.compareTo(BigDecimal.ZERO) == 0 && surplusUsed.compareTo(BigDecimal.ZERO) > 0) {
            return TargetPeriodResultType.SURPLUS_USED;
        } else {
            return null;
        }
    };
}
