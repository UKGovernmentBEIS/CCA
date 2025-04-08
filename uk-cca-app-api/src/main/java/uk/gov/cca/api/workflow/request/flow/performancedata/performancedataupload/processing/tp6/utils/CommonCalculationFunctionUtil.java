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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@UtilityClass
public class CommonCalculationFunctionUtil {

    // tp_energy*IF(energy_carbon_unit="kg",tp_carbon_factor,IF(energy_carbon_unit="tonne",tp_carbon_factor/1000,1)
    public final TriFunction<BigDecimal, BigDecimal, MeasurementType, BigDecimal> ENERGY_CARBON =
            (tpEnergy, tpCarbonFactor, energyCarbonUnit) ->
                switch (energyCarbonUnit) {
                    case ENERGY_KWH, ENERGY_MWH, ENERGY_GJ -> tpEnergy;
                    case CARBON_KG -> tpEnergy.multiply(tpCarbonFactor, MathContext.DECIMAL128);
                    case CARBON_TONNE -> tpEnergy.multiply(tpCarbonFactor, MathContext.DECIMAL128).divide(BigDecimal.valueOf(1000), MathContext.DECIMAL128);
                };

    // IF(energy_carbon_unit="kg",1/1000,IF(energy_carbon_unit="tonne",1,tp_carbon_factor))
    public final BiFunction<BigDecimal, MeasurementType, BigDecimal> CARBON =
            (tpCarbonFactor, energyCarbonUnit) ->
                switch (energyCarbonUnit) {
                    case ENERGY_KWH, ENERGY_MWH, ENERGY_GJ -> tpCarbonFactor;
                    case CARBON_KG -> BigDecimal.ONE.divide(BigDecimal.valueOf(1000), MathContext.DECIMAL128);
                    case CARBON_TONNE -> BigDecimal.ONE;
                };

    // SUMPRODUCT(energy_data,carbon_factors)
    public final FourFunction<Map<FixedConversionFactor, BigDecimal>, List<OtherFuel>, MeasurementType, PerformanceDataTargetPeriodType, BigDecimal> SUM_PRODUCT =
            (energyData, carbonFactors, measurementType, type) -> {
        BigDecimal energy = energyData.entrySet().stream()
                .filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
                .map(entry ->
                        type.equals(PerformanceDataTargetPeriodType.TP6)
                                ? FixedConversionFactor.getTP6ValueByMeasurementType(entry.getKey(), measurementType).multiply(entry.getValue(), MathContext.DECIMAL128)
                                : BigDecimal.ZERO
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal carbon = carbonFactors.stream()
                .filter(entry -> !ObjectUtils.isEmpty(entry.getConversionFactor()) && !ObjectUtils.isEmpty(entry.getConsumption()))
                .map(entry -> entry.getConversionFactor().multiply(entry.getConsumption(), MathContext.DECIMAL128))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return energy.add(carbon);
    };

    // IF(target_type="Novem",1-(target_energy_carbon_tp_throughput/IF(by_energy_carbon_tp_throughput=0,1,by_energy_carbon_tp_throughput)),percent_target)
    public final BiFunction<BigDecimal, BigDecimal, Optional<BigDecimal>> NOVEM_PERCENT_TARGET_GROUP_SURPLUS =
            (targetEnergyCarbonTpThroughput, byEnergyCarbonTpThroughput) -> {
                if(ObjectUtils.isEmpty(targetEnergyCarbonTpThroughput) || ObjectUtils.isEmpty(byEnergyCarbonTpThroughput)) {
                    return Optional.empty();
                }

                // (target_energy_carbon_tp_throughput/IF(by_energy_carbon_tp_throughput=0,1,by_energy_carbon_tp_throughput)
                BigDecimal group = byEnergyCarbonTpThroughput.compareTo(BigDecimal.ZERO) !=0
                        ? targetEnergyCarbonTpThroughput.divide(byEnergyCarbonTpThroughput, MathContext.DECIMAL128)
                        : targetEnergyCarbonTpThroughput.divide(BigDecimal.ONE, MathContext.DECIMAL128);

                return Optional.of(BigDecimal.ONE.subtract(group, MathContext.DECIMAL128));
            };
}
