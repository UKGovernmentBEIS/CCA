package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.common.domain.FourFunction;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.TriFunction;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityProductVariableEnergyData;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;

@UtilityClass
public class PerformanceDataFacilityCalculationCommonFunctionUtil {
	
	public final BigDecimal NON_STANDARD_FUEL_PRIMARY_FACTOR = BigDecimal.ONE;

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

    /**
     * Total baseline variable energy divided by Total baseline throughput
     */
    public final BinaryOperator<BigDecimal> ENERGY_CARBON_INTENSITY =
            (baselineVariableEnergy, totalThroughput) ->
                    totalThroughput.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : baselineVariableEnergy.divide(totalThroughput, MathContext.DECIMAL128);

    /**
     * Throughput adjustment factor = Delivered energy / (Delivered energy + Electricity from CHP)
     * For GRID_ELECTRICITY && NON_GRID_ELECTRICITY
     * If both “Delivered energy” and “Electricity from CHP” are zero, value will = 1
     */
    public final BiFunction<Map<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption>, BigDecimal, BigDecimal> THROUGHPUT_ADJUSTMENT_FACTOR =
            (standardFuels, electricitySuppliedFromCHP) -> {
        BigDecimal standardFuelsDeliveredEnergy = standardFuels.entrySet().stream()
                .filter(f ->
                        f.getKey().equals(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY) ||
                                f.getKey().equals(PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY)
                )
                .map(entry -> entry.getValue().getDeliveredEnergy())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal deliveredEnergySumElectricitySuppliedCHP = standardFuelsDeliveredEnergy.add(electricitySuppliedFromCHP);

        return standardFuelsDeliveredEnergy.compareTo(BigDecimal.ZERO) == 0 && electricitySuppliedFromCHP.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ONE
                : standardFuelsDeliveredEnergy.divide(deliveredEnergySumElectricitySuppliedCHP, MathContext.DECIMAL128);
    };

    /**
     * SRM is applicable, Adjusted throughput = Throughput adjustment factor x Actual throughput ELSE
     * Adjusted throughput = Actual throughput
     */
    public final TriFunction<Boolean, PerformanceDataFacilityInputEnergyFuelDetails, BigDecimal, BigDecimal> ADJUSTED_THROUGHPUT =
            (usedReportingMechanism, fuelDetails, actualThroughput) -> {
                if(Boolean.TRUE.equals(usedReportingMechanism)) {
                    BigDecimal factor = THROUGHPUT_ADJUSTMENT_FACTOR
                            .apply(fuelDetails.getStandardFuels(), fuelDetails.getElectricitySuppliedFromCHP());

                    return factor.multiply(actualThroughput, MathContext.DECIMAL128);
                }

                return actualThroughput;
    };

    /**
     * MAX(0,(facilityTarget for TP -progressAtProductBaseYear)/ (1-progressAtProductBaseYear))
     * For interim reports, replace the facilityTarget for TP in the formula
     * If base year = facility base year, display facility improvement
     * progressAtProductBaseYear = ((MIN(productBaseYear,2026)-facilityBaseYear)/(2026-facilityBaseYear)*facilityTargetTP7)
     * +(MAX(MIN(productBaseYear,2028)-2026,0)/(2028-2026)*(facilityTargetTP8-facilityTargetTP7))
     * +(MAX(MIN(productBaseYear,2030)-2028,0)/(2030-2028)*(facilityTargetTP9-facilityTargetTP8))
     */
    public final BiFunction<PerformanceDataFacilityCalculationParameters, ProductVariableEnergyConsumptionData, BigDecimal> PRODUCT_TARGET_IMPROVEMENT =
            (calculatedParameters, originalProduct) -> {
        if(calculatedParameters.getBaselineDate().getYear() == originalProduct.getBaselineYear().getValue()) {
            return calculatedParameters.getTargetImprovement();
        }

        BigDecimal productBaseYear = BigDecimal.valueOf(originalProduct.getBaselineYear().getValue());
        BigDecimal facilityBaseYear = BigDecimal.valueOf(calculatedParameters.getBaselineDate().getYear());

        BigDecimal progressAtProductBaseYear = BigDecimal.ZERO;
        for (Map.Entry<TargetPeriodType, Integer> entry : calculatedParameters.getLastYearPerTp().entrySet()) {
            Optional<TargetImprovementType> targetImprovementTypeTp = Arrays.stream(TargetImprovementType.values())
                    .filter(improvement -> improvement.name().equals(entry.getKey().name()))
                    .findFirst();
            BigDecimal facilityTargetTP = targetImprovementTypeTp.map(imp -> calculatedParameters.getImprovements().get(imp)).orElse(BigDecimal.ZERO)
                    .divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);

            Optional<TargetImprovementType> previous = TargetImprovementType
                    .getTargetImprovementTypeByTargetPeriodNumber(targetImprovementTypeTp.map(TargetImprovementType::getTargetPeriodNumber).orElse(0) - 1);
            BigDecimal previousFacilityTargetTP = previous
                    .map(targetImprovementType -> calculatedParameters.getImprovements().get(targetImprovementType)
                            .divide(BigDecimal.valueOf(100), MathContext.DECIMAL128))
                    .orElse(BigDecimal.ZERO);
            BigDecimal previousYear = previous.map(targetImprovementType -> {
                Optional<TargetPeriodType> previousTp = Arrays.stream(TargetPeriodType.values())
                        .filter(tp -> tp.name().equals(targetImprovementType.name())).findFirst();
                return previousTp.map(tp -> BigDecimal.valueOf(calculatedParameters.getLastYearPerTp().get(tp)))
                        .orElse(BigDecimal.ZERO);
            }).orElse(facilityBaseYear);

            BigDecimal progressAtProductBaseYearTP = productBaseYear.min(BigDecimal.valueOf(entry.getValue()))
                    .subtract(previousYear).max(BigDecimal.ZERO)
                    .divide(BigDecimal.valueOf(entry.getValue()).subtract(previousYear), MathContext.DECIMAL128)
                    .multiply(facilityTargetTP.subtract(previousFacilityTargetTP, MathContext.DECIMAL128), MathContext.DECIMAL128);

            progressAtProductBaseYear = progressAtProductBaseYear.add(progressAtProductBaseYearTP, MathContext.DECIMAL128);
        }

        BigDecimal result = (calculatedParameters.getTargetImprovement().subtract(progressAtProductBaseYear, MathContext.DECIMAL128))
                .divide(BigDecimal.ONE.subtract(progressAtProductBaseYear, MathContext.DECIMAL128), MathContext.DECIMAL128);

        return result.compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO
                : result;
    };
    
    public final FourFunction<PerformanceDataFacilityCalculationParameters, ProductVariableEnergyConsumptionData, PerformanceDataFacilityInputEnergyFuelDetails, BigDecimal, BigDecimal> PRODUCT_ENERGY_ =
            (calculatedParameters, originalProduct, fuelDetails, actualThroughput) -> {

        Boolean usedReportingMechanism = calculatedParameters.getUsedReportingMechanism();
        BigDecimal productImprovement = PRODUCT_TARGET_IMPROVEMENT.apply(calculatedParameters, originalProduct);
        BigDecimal improvementSubtract = BigDecimal.ONE.subtract(productImprovement);
        BigDecimal actualThroughputOrZero = Optional.ofNullable(actualThroughput).orElse(BigDecimal.ZERO);
        BigDecimal adjustedThroughput = PerformanceDataFacilityCalculationCommonFunctionUtil.ADJUSTED_THROUGHPUT.apply(usedReportingMechanism, fuelDetails, actualThroughputOrZero);

        return originalProduct.getEnergyCarbonIntensity()
                .multiply(adjustedThroughput, MathContext.DECIMAL128)
                .multiply(improvementSubtract, MathContext.DECIMAL128);
    };

    /**
     * (BY energy/carbon intensity x Adjusted throughput) x (1 - improvement % or Interim target %)
     */
    public final TriFunction<PerformanceDataFacilityCalculationParameters, ProductVariableEnergyConsumptionData, PerformanceDataFacilityInputData, BigDecimal> PRODUCT_ENERGY =
            (calculatedParameters, originalProduct, data) -> {
        // Get product's details
        Optional<PerformanceDataFacilityProductVariableEnergyData> productVariable = data.getThroughputDetails().getVariableEnergyConsumptionDataByProduct().stream()
                .filter(p -> ObjectUtils.compare(p.getProductName(), originalProduct.getProductName()) == 0)
                .findFirst();
        
        BigDecimal actualThroughput = productVariable.map(PerformanceDataFacilityProductVariableEnergyData::getActualThroughput)
                .orElse(BigDecimal.ZERO);
        
        return PRODUCT_ENERGY_.apply(calculatedParameters, originalProduct, data.getEnergyFuelDetails(), actualThroughput);
    };

    /**
     * Sum[(BY energy/carbon intensity x Adjusted throughput) x (1 - improvement % or Interim target %)]
     */
    public final BiFunction<PerformanceDataFacilityCalculationParameters, PerformanceDataFacilityInputData, BigDecimal> PRODUCT_SUM_ENERGY_ADJUSTED_THROUGHPUT =
            (calculatedParameters, data) ->
        calculatedParameters.getVariableEnergyConsumptionDataByProduct().stream()
                .map(originalProduct ->
                        PerformanceDataFacilityCalculationCommonFunctionUtil.PRODUCT_ENERGY
                                .apply(calculatedParameters, originalProduct, data)
                ).reduce(BigDecimal.ZERO, BigDecimal::add);

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
     * Split by product: Sum of [(BY energy/carbon intensity x Adjusted throughput for each product) x (1 - improvement % or Interim target %)]
     * Variable totals only: (BY energy/carbon intensity x Adjusted throughput) x (1 - improvement % or Interim target %)
     * Fixed only: BY fixed energy/carbon
     */
    public final BiFunction<PerformanceDataFacilityCalculationParameters, PerformanceDataFacilityInputData, BigDecimal> TOTAL_TARGET_VARIABLE_ENERGY =
            (calculatedParameters, data) -> {
                if (calculatedParameters.getVariableEnergyType() == null) {
                    return BigDecimal.ZERO;
                }

                return switch (calculatedParameters.getVariableEnergyType()) {
                    case BY_PRODUCT ->
                    	PerformanceDataFacilityCalculationCommonFunctionUtil.PRODUCT_SUM_ENERGY_ADJUSTED_THROUGHPUT.apply(calculatedParameters, data);
                    case TOTALS -> {
                    	Boolean usedReportingMechanism = calculatedParameters.getUsedReportingMechanism();
                        BigDecimal energyCarbonIntensity = PerformanceDataFacilityCalculationCommonFunctionUtil.ENERGY_CARBON_INTENSITY
                                .apply(calculatedParameters.getBaselineVariableEnergy(), calculatedParameters.getTotalThroughput());
                        BigDecimal adjustedThroughput = PerformanceDataFacilityCalculationCommonFunctionUtil.ADJUSTED_THROUGHPUT
                                .apply(usedReportingMechanism, data.getEnergyFuelDetails(), data.getThroughputDetails().getActualThroughput());

                        yield energyCarbonIntensity
                        	.multiply(adjustedThroughput, MathContext.DECIMAL128)
                        	.multiply(BigDecimal.ONE.subtract(calculatedParameters.getTargetImprovement()), MathContext.DECIMAL128);
                    }
                };
            };

    /**
     * Split by product: Sum of (BY energy/carbon intensity x Adjusted throughput for each product)
     * Variable totals only: (BY energy/carbon intensity x Adjusted throughput)
     * Fixed only: BY fixed energy/carbon
     */
    public final BiFunction<PerformanceDataFacilityCalculationParameters, PerformanceDataFacilityInputData, BigDecimal> ENERGY_INTENSITY_MULTIPLY_ADJUSTED_THROUGHPUT =
            (calculatedParameters, data) -> {
        Boolean usedReportingMechanism = calculatedParameters.getUsedReportingMechanism();

        if (calculatedParameters.getVariableEnergyType() == null) {
            return BigDecimal.ZERO;
        }

        return switch (calculatedParameters.getVariableEnergyType()) {
            case BY_PRODUCT ->
                    calculatedParameters.getVariableEnergyConsumptionDataByProduct().stream()
                        .map(originalProduct -> {
                            // Get product's details
                            Optional<PerformanceDataFacilityProductVariableEnergyData> productVariable = data.getThroughputDetails().getVariableEnergyConsumptionDataByProduct().stream()
                                    .filter(p -> ObjectUtils.compare(p.getProductName(), originalProduct.getProductName()) == 0)
                                    .findFirst();
                            BigDecimal actualThroughput = productVariable.map(PerformanceDataFacilityProductVariableEnergyData::getActualThroughput)
                                    .orElse(BigDecimal.ZERO);

                            return originalProduct.getEnergyCarbonIntensity()
                                    .multiply(PerformanceDataFacilityCalculationCommonFunctionUtil.ADJUSTED_THROUGHPUT
                                            .apply(usedReportingMechanism, data.getEnergyFuelDetails(), actualThroughput), MathContext.DECIMAL128);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
            case TOTALS -> {
                BigDecimal energyCarbonIntensity = PerformanceDataFacilityCalculationCommonFunctionUtil.ENERGY_CARBON_INTENSITY
                        .apply(calculatedParameters.getBaselineVariableEnergy(), calculatedParameters.getTotalThroughput());
                BigDecimal adjustedThroughput = PerformanceDataFacilityCalculationCommonFunctionUtil.ADJUSTED_THROUGHPUT
                        .apply(usedReportingMechanism, data.getEnergyFuelDetails(), data.getThroughputDetails().getActualThroughput());

                yield energyCarbonIntensity.multiply(adjustedThroughput, MathContext.DECIMAL128);
            }
        };
    };

    /**
     * Split by product: Sum of (BY energy/carbon intensity x Adjusted throughput for each product) + (BY fixed energy/carbon if applicable)
     * Variable totals only: (BY energy/carbon intensity x Adjusted throughput) + (BY fixed energy/carbon if applicable)
     * Fixed only: BY fixed energy/carbon
     * multiplier -> (adjust BY fixed energy/carbon to 2 x BY Fixed energy/carbon for 2 year target period)
     */
    public final BiFunction<PerformanceDataFacilityCalculationParameters, PerformanceDataFacilityInputData, BigDecimal> TOTAL_BY_ENERGY_CARBON_AT_TP_THROUGHPUT =
            (calculatedParameters, data) -> {
        BigDecimal tpMultiplier = calculatedParameters.getTpMultiplier();
        BigDecimal byFixedEnergy = tpMultiplier.multiply(calculatedParameters.getTotalFixedEnergy(), MathContext.DECIMAL128);

        if (calculatedParameters.getVariableEnergyType() == null) {
            return byFixedEnergy;
        }

        return PerformanceDataFacilityCalculationCommonFunctionUtil.ENERGY_INTENSITY_MULTIPLY_ADJUSTED_THROUGHPUT
                .apply(calculatedParameters, data)
                .add(byFixedEnergy);
    };
}
