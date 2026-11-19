package uk.gov.cca.api.workflow.request.flow.common.utils;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceEnergyFuelDetails;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@UtilityClass
public class PerformanceFuelEnergyCalculationCommonFunctionUtil {

    public final BigDecimal NON_STANDARD_FUEL_PRIMARY_FACTOR = BigDecimal.ONE;

    /**
     * Primary energy = Delivered energy x Primary conversion factor
     * Primary carbon = Delivered energy x Primary conversion factor x CO2 conversion factor
     */
    public final BiFunction<Map.Entry<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption>, MeasurementType, BigDecimal> PRIMARY_ENERGY_STANDARD_FUEL =
            (entry, measurementType) ->
                    switch (measurementType) {
                        case ENERGY_KWH, ENERGY_MWH, ENERGY_GJ ->
                                entry.getValue().getDeliveredEnergy().multiply(entry.getKey().getPrimaryFactor(), MathContext.DECIMAL128);
                        case CARBON_KG -> entry.getValue().getDeliveredEnergy()
                                .multiply(entry.getKey().getPrimaryFactor(), MathContext.DECIMAL128)
                                .multiply(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(entry.getKey(), measurementType), MathContext.DECIMAL128);
                        case CARBON_TONNE -> entry.getValue().getDeliveredEnergy()
                                .multiply(entry.getKey().getPrimaryFactor(), MathContext.DECIMAL128)
                                .multiply(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(entry.getKey(), measurementType), MathContext.DECIMAL128)
                                .divide(BigDecimal.valueOf(1000), 20, RoundingMode.HALF_UP);
                    };

    /**
     * Primary energy = Delivered energy x Primary conversion factor
     * Primary carbon = Delivered energy x Primary conversion factor x CO2 conversion factor
     */
    public final BiFunction<PerformanceDataFacilityNonStandardFuel, MeasurementType, BigDecimal> PRIMARY_ENERGY_NON_STANDARD_FUEL =
            (nonStandardFuel, measurementType) ->
                    switch (measurementType) {
                        case ENERGY_KWH, ENERGY_MWH, ENERGY_GJ -> nonStandardFuel.getDeliveredEnergy()
                                .multiply(NON_STANDARD_FUEL_PRIMARY_FACTOR, MathContext.DECIMAL128);
                        case CARBON_KG -> nonStandardFuel.getDeliveredEnergy()
                                .multiply(NON_STANDARD_FUEL_PRIMARY_FACTOR, MathContext.DECIMAL128)
                                .multiply(nonStandardFuel.getConversionFactor(), MathContext.DECIMAL128);
                        case CARBON_TONNE -> nonStandardFuel.getDeliveredEnergy()
                                .multiply(NON_STANDARD_FUEL_PRIMARY_FACTOR, MathContext.DECIMAL128)
                                .multiply(nonStandardFuel.getConversionFactor(), MathContext.DECIMAL128)
                                .divide(BigDecimal.valueOf(1000), 20, RoundingMode.HALF_UP);
                    };

    /**
     * Sum of Primary energy/carbon for all fuels where the value is not zero
     */
    public final BiFunction<PerformanceEnergyFuelDetails, MeasurementType, BigDecimal> ACTUAL_ENERGY_CARBON =
            (energyFuelDetails, measurementType) -> {
                if (energyFuelDetails == null) {
                    return BigDecimal.ZERO;
                }

                BigDecimal sumOfStandardFuels = PerformanceFuelEnergyCalculationCommonFunctionUtil.TOTAL_STANDARD_FUELS_DELIVERED_ENERGY.apply(energyFuelDetails.getStandardFuels(),
                        entry -> PerformanceFuelEnergyCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL.apply(entry, measurementType));

                BigDecimal sumOfNonStandardFuels = PerformanceFuelEnergyCalculationCommonFunctionUtil.TOTAL_NON_STANDARD_FUELS_DELIVERED_ENERGY.apply(energyFuelDetails.getNonStandardFuels(),
                        fuel -> PerformanceFuelEnergyCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL.apply(fuel, measurementType));

                return sumOfStandardFuels.add(sumOfNonStandardFuels);
            };

    /**
     * Energy based: For each fuel where Total consumption (1) value is not blank, tCO2e =Total consumption x Conversion factor / 1000= tCO2e for the specific fuel
     * Carbon based: For each fuel where Total consumption (1) value is not blank, tCO2e =Total consumption/ 1000= tCO2e for the specific fuel
     */
    public final BiFunction<PerformanceEnergyFuelDetails, MeasurementType, BigDecimal> ACTUAL_TCO2_EMISSIONS =
            (fuelDetails, measurementType) -> {
                if (fuelDetails == null) {
                    return BigDecimal.ZERO;
                }
                return switch (measurementType) {
                    case ENERGY_KWH, ENERGY_MWH, ENERGY_GJ -> {
                        BigDecimal sumOfStandardFuels = PerformanceFuelEnergyCalculationCommonFunctionUtil.TOTAL_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getStandardFuels(),
                                entry -> PerformanceFuelEnergyCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL
                                        .apply(entry, measurementType)
                                        .multiply(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(entry.getKey(), measurementType), MathContext.DECIMAL128));

                        BigDecimal sumOfNonStandardFuels = PerformanceFuelEnergyCalculationCommonFunctionUtil.TOTAL_NON_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getNonStandardFuels(),
                                fuel -> PerformanceFuelEnergyCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL
                                        .apply(fuel, measurementType)
                                        .multiply(fuel.getConversionFactor(), MathContext.DECIMAL128));

                        yield sumOfStandardFuels.add(sumOfNonStandardFuels).divide(BigDecimal.valueOf(1000), MathContext.DECIMAL128);
                    }
                    case CARBON_KG -> {
                        BigDecimal sumOfStandardFuels = PerformanceFuelEnergyCalculationCommonFunctionUtil.TOTAL_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getStandardFuels(),
                                entry -> PerformanceFuelEnergyCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL
                                        .apply(entry, measurementType)
                                        .divide(BigDecimal.valueOf(1000), 20, RoundingMode.HALF_UP));

                        BigDecimal sumOfNonStandardFuels = PerformanceFuelEnergyCalculationCommonFunctionUtil.TOTAL_NON_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getNonStandardFuels(),
                                fuel -> PerformanceFuelEnergyCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL
                                        .apply(fuel, measurementType)
                                        .divide(BigDecimal.valueOf(1000), 20, RoundingMode.HALF_UP));


                        yield sumOfStandardFuels.add(sumOfNonStandardFuels);
                    }
                    case CARBON_TONNE -> {
                        BigDecimal sumOfStandardFuels = PerformanceFuelEnergyCalculationCommonFunctionUtil.TOTAL_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getStandardFuels(),
                                entry -> PerformanceFuelEnergyCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL
                                        .apply(entry, measurementType));

                        BigDecimal sumOfNonStandardFuels = PerformanceFuelEnergyCalculationCommonFunctionUtil.TOTAL_NON_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getNonStandardFuels(),
                                fuel -> PerformanceFuelEnergyCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL
                                        .apply(fuel, measurementType));

                        yield sumOfStandardFuels.add(sumOfNonStandardFuels);
                    }
                };
            };

    public final BiFunction<Map<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption>, Function<Map.Entry<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption>, BigDecimal>, BigDecimal> TOTAL_STANDARD_FUELS_DELIVERED_ENERGY =
            (standardFuels, predicate) ->
                    standardFuels.entrySet().stream()
                            .filter(data -> data.getValue() != null)
                            .map(predicate)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

    public final BiFunction<List<PerformanceDataFacilityNonStandardFuel>, Function<PerformanceDataFacilityNonStandardFuel, BigDecimal>, BigDecimal> TOTAL_NON_STANDARD_FUELS_DELIVERED_ENERGY =
            (nonStandardFuels, predicate) ->
                    nonStandardFuels.stream()
                            .filter(fuel -> fuel.getDeliveredEnergy() != null)
                            .map(predicate)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
}
