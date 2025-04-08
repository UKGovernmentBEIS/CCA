package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.common.domain.FourFunction;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.TriFunction;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.OtherFuel;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.FixedConversionFactor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@UtilityClass
public class PrimaryDeterminationCalculationFunctionUtil {

    // IF(tp_energy=0,0,SUMPRODUCT(energy_data,carbon_factors)/tp_energy)
    public final FourFunction<Map<FixedConversionFactor, BigDecimal>, List<OtherFuel>, BigDecimal, MeasurementType, Optional<BigDecimal>> TP6_TARGET_PERIOD_CARBON_FACTOR =
            (energyData, carbonFactors, tpEnergy, measurementType) -> {
        if(ObjectUtils.isEmpty(tpEnergy) || ObjectUtils.isEmpty(measurementType)) {
            return Optional.empty();
        }

        if(tpEnergy.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.of(BigDecimal.ZERO);
        }

        BigDecimal sumProduct = CommonCalculationFunctionUtil.SUM_PRODUCT
                .apply(energyData, carbonFactors, measurementType, PerformanceDataTargetPeriodType.TP6);

        return Optional.of(sumProduct.divide(tpEnergy, MathContext.DECIMAL128));
    };

    // numerical_target - tp_performance
    public final BiFunction<BigDecimal, BigDecimal, Optional<BigDecimal>> ABSOLUTE_AMOUNT_ENERGY_USED_UNDER_TARGET =
            (numericalTarget, tpPerformance) -> {
        if(ObjectUtils.isEmpty(numericalTarget) || ObjectUtils.isEmpty(tpPerformance)) {
            return Optional.empty();
        }
        return Optional.of(numericalTarget.subtract(tpPerformance, MathContext.DECIMAL128));
    };

    // tolerance * IF(tp_performance_percent>percent_target,-1,1)
    public final TriFunction<BigDecimal, BigDecimal, BigDecimal, Optional<BigDecimal>> RELATIVE_GROUP2_AMOUNT_ENERGY_USED_UNDER_TARGET =
            (tolerance, tpPerformancePercent, percentTarget) -> {
        if(ObjectUtils.isEmpty(tolerance) || ObjectUtils.isEmpty(tpPerformancePercent)
                || ObjectUtils.isEmpty(percentTarget)) {
            return Optional.empty();
        }
        BigDecimal factor = tpPerformancePercent.compareTo(percentTarget) > 0
                ? BigDecimal.valueOf(-1)
                : BigDecimal.ONE;

        return Optional.of(tolerance.multiply(factor, MathContext.DECIMAL128));
    };

    // (numerical_target+tolerance*IF(tp_performance_percent>percent_target,-1,1)-tp_performance)*reporting_throughput
    public final FourFunction<BigDecimal, BigDecimal, BigDecimal, BigDecimal, Optional<BigDecimal>> RELATIVE_AMOUNT_ENERGY_USED_UNDER_TARGET =
            (numericalTarget, group2, tpPerformance, reportingThroughput) -> {
        if(ObjectUtils.isEmpty(numericalTarget) || ObjectUtils.isEmpty(group2)
                || ObjectUtils.isEmpty(tpPerformance) || ObjectUtils.isEmpty(reportingThroughput)) {
            return Optional.empty();
        }

        BigDecimal group1 = numericalTarget.add(group2).subtract(tpPerformance);
        return Optional.of(group1.multiply(reportingThroughput, MathContext.DECIMAL128));
    };

    // target_energy_carbon_tp_throughput-tp_energy*IF(energy_carbon_unit="kg",tp_carbon_factor,IF(energy_carbon_unit="tonne",tp_carbon_factor/1000,1))
    public final FourFunction<BigDecimal, BigDecimal, BigDecimal, MeasurementType, Optional<BigDecimal>> NOVEM_AMOUNT_ENERGY_USED_UNDER_TARGET =
            (targetEnergyCarbonTpThroughput, tpEnergy, tpCarbonFactor, energyCarbonUnit) -> {
        if(ObjectUtils.isEmpty(targetEnergyCarbonTpThroughput) || ObjectUtils.isEmpty(tpEnergy)
                || ObjectUtils.isEmpty(energyCarbonUnit) || ObjectUtils.isEmpty(tpCarbonFactor)) {
            return Optional.empty();
        }

        BigDecimal energy = CommonCalculationFunctionUtil.ENERGY_CARBON.apply(tpEnergy, tpCarbonFactor, energyCarbonUnit);

        return Optional.of(targetEnergyCarbonTpThroughput.subtract(energy, MathContext.DECIMAL128));
    };

    // energy_carbon_under_target*IF(energy_carbon_unit="kg",1/1000,IF(energy_carbon_unit="tonne",1,tp_carbon_factor))*44/12
    public final TriFunction<BigDecimal, BigDecimal, MeasurementType, Optional<BigDecimal>> AMOUNT_CO2_EMITTED_UNDER_TARGET =
            (energyCarbonUnderTarget, tpCarbonFactor, energyCarbonUnit) -> {
        if(ObjectUtils.isEmpty(energyCarbonUnderTarget) || ObjectUtils.isEmpty(tpCarbonFactor) || ObjectUtils.isEmpty(energyCarbonUnit)) {
            return Optional.empty();
        }
        BigDecimal carbon = CommonCalculationFunctionUtil.CARBON.apply(tpCarbonFactor, energyCarbonUnit);

        return Optional.of(energyCarbonUnderTarget.multiply(carbon, MathContext.DECIMAL128)
                .multiply(BigDecimal.valueOf(44), MathContext.DECIMAL128)
                .divide(BigDecimal.valueOf(12), MathContext.DECIMAL128));
    };

    // SUMPRODUCT(energy_data,carbon_factors)*44/12*IF(OR(energy_carbon_unit="kg",energy_carbon_unit="tonne"),0.001,1)
    public final FourFunction<Map<FixedConversionFactor, BigDecimal>, List<OtherFuel>, MeasurementType, PerformanceDataTargetPeriodType, Optional<BigDecimal>> CARBON_DIOXIDE_EMITTED =
            (energyData, carbonFactors, measurementType, type) -> {
        if(ObjectUtils.isEmpty(measurementType)) {
            return Optional.empty();
        }
        BigDecimal sumProduct = CommonCalculationFunctionUtil.SUM_PRODUCT
                .apply(energyData, carbonFactors, measurementType, type);
        BigDecimal multiplier = switch (measurementType) {
            case CARBON_TONNE, CARBON_KG -> BigDecimal.valueOf(0.001);
            case ENERGY_KWH, ENERGY_MWH, ENERGY_GJ -> BigDecimal.ONE;
        };

        return Optional.of(sumProduct.multiply(BigDecimal.valueOf(44), MathContext.DECIMAL128)
                .divide(BigDecimal.valueOf(12), MathContext.DECIMAL128)
                .multiply(multiplier, MathContext.DECIMAL128));
    };

    // IF(tp_performance_percent<percent_target,IF(OR(target_period="TP5",target_period="TP6"),0,IF(banked_surplus>ROUNDUP(ABS(carbon_under_target),0),ROUNDUP(ABS(carbon_under_target),0),banked_surplus)),0)
    public final FourFunction<BigDecimal, BigDecimal, BigDecimal, BigDecimal, Optional<BigDecimal>> TP6_BANKED_SURPLUS_USED =
            (tpPerformancePercent, percentTarget, bankedSurplus, carbonUnderTarget) -> {
        if(ObjectUtils.isEmpty(tpPerformancePercent) || ObjectUtils.isEmpty(percentTarget)
                || ObjectUtils.isEmpty(bankedSurplus) || ObjectUtils.isEmpty(carbonUnderTarget)) {
            return Optional.empty();
        }
        else if(tpPerformancePercent.compareTo(percentTarget) < 0) {
            // target_period="TP5", target_period="TP6"
            return Optional.of(BigDecimal.ZERO);
        } else {
            BigDecimal integerCarbonUnderTarget = carbonUnderTarget.abs().setScale(0, RoundingMode.CEILING);
            return bankedSurplus.compareTo(integerCarbonUnderTarget) > 0
                    ? Optional.of(integerCarbonUnderTarget)
                    : Optional.of(bankedSurplus);
        }
    };

    // ROUNDDOWN(IF(tp_performance_percent>percent_target,carbon_under_target,0),0)
    public final TriFunction<BigDecimal, BigDecimal, BigDecimal, Optional<BigDecimal>> SURPLUS_GAINED =
            (tpPerformancePercent, percentTarget, carbonUnderTarget) -> {
        if(ObjectUtils.isEmpty(tpPerformancePercent) || ObjectUtils.isEmpty(percentTarget) || ObjectUtils.isEmpty(carbonUnderTarget)) {
            return Optional.empty();
        }
        return tpPerformancePercent.compareTo(percentTarget) > 0
                ? Optional.of(carbonUnderTarget.setScale(0, RoundingMode.DOWN))
                : Optional.of(BigDecimal.ZERO);
    };

    // =IF(IF(OR(target_period="TP5",target_period="TP6"),0,-banked_surplus)+ROUNDUP((IF(energy_carbon_under_target<0,ABS(energy_carbon_under_target)*IF(energy_carbon_unit="kg",1/1000,tp_carbon_factor),0)*44/12),0)<0,0,IF(OR(target_period="TP5",target_period="TP6"),0,-banked_surplus)+ROUNDUP((IF(energy_carbon_under_target<0,ABS(energy_carbon_under_target)*IF(energy_carbon_unit="kg",1/1000,IF(energy_carbon_unit="tonne",1,tp_carbon_factor)),0)*44/12),0))
    public final FourFunction<BigDecimal, BigDecimal, BigDecimal, MeasurementType, Optional<BigDecimal>> TP6_BUY_OUT_REQUIRED =
            (bankedSurplus, energyCarbonUnderTarget, tpCarbonFactor, energyCarbonUnit) -> {
        if(ObjectUtils.isEmpty(energyCarbonUnderTarget) || ObjectUtils.isEmpty(tpCarbonFactor) || ObjectUtils.isEmpty(energyCarbonUnit)) {
            return Optional.empty();
        }

        // IF(energy_carbon_unit="kg",1/1000,IF(energy_carbon_unit="tonne",1,tp_carbon_factor))
        BigDecimal carbon = CommonCalculationFunctionUtil.CARBON.apply(tpCarbonFactor, energyCarbonUnit);

        // (IF(energy_carbon_under_target<0,ABS(energy_carbon_under_target)*IF(energy_carbon_unit="kg",1/1000,tp_carbon_factor),0)*44/12)
        BigDecimal group = energyCarbonUnderTarget.compareTo(BigDecimal.ZERO) < 0
                ? energyCarbonUnderTarget.abs()
                    .multiply(carbon, MathContext.DECIMAL128)
                    .multiply(BigDecimal.valueOf(44), MathContext.DECIMAL128)
                    .divide(BigDecimal.valueOf(12), MathContext.DECIMAL128)
                : BigDecimal.ZERO;

        // OR(target_period="TP5",target_period="TP6"),0,-banked_surplus)+ROUNDUP((IF(energy_carbon_under_target<0,ABS(energy_carbon_under_target)*IF(energy_carbon_unit="kg",1/1000,tp_carbon_factor),0)*44/12),0)
        BigDecimal result = BigDecimal.ZERO.add(group.setScale(0, RoundingMode.CEILING));

        return result.compareTo(BigDecimal.ZERO) < 0
                ? Optional.of(BigDecimal.ZERO)
                : Optional.of(result);
    };

    // =ROUND(pri_buy_out_carbon*IFS(
    //target_period="TP6",25,
    //target_period="TP5",18,
    //OR(target_period="TP3",target_period="TP4"),14,
    //OR(target_period="TP1",target_period="TP2"),12),0)
    public final BiFunction<BigDecimal, BigDecimal, Optional<BigDecimal>> BUY_OUT_COST =
            (priBuyOutCarbon, multiplier) -> {
        if(ObjectUtils.isEmpty(priBuyOutCarbon)) {
            return Optional.empty();
        }

        return Optional.of(priBuyOutCarbon.multiply(multiplier, MathContext.UNLIMITED));
    };
}
