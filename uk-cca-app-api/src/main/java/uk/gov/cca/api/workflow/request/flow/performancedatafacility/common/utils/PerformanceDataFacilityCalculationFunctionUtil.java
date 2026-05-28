package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.TriFunction;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityReferenceData;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@UtilityClass
public class PerformanceDataFacilityCalculationFunctionUtil {

    public BigDecimal getTargetImprovement(PerformanceDataReportType reportType, TargetPeriodType targetPeriodType,
                                           PerformanceDataFacilityReferenceData referenceData) {
        final Map<TargetImprovementType, BigDecimal> improvements = referenceData.getBaselineAndTargets().getImprovements();

        if(reportType.equals(PerformanceDataReportType.FINAL)) {
            TargetImprovementType targetImprovementType = Arrays.stream(TargetImprovementType.values())
                    .filter(improvement -> improvement.name().equals(targetPeriodType.name()))
                    .findFirst().orElse(null);

            return targetImprovementType == null
                    ? BigDecimal.ZERO
                    : improvements.get(targetImprovementType).divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
        }

        return PerformanceDataFacilityCalculationFunctionUtil.INTERIM_TARGET
                .apply(improvements, targetPeriodType)
                .setScale(7, RoundingMode.HALF_UP);
    }

    public final Function<PerformanceDataFacilityCalculationParameters, BigDecimal> TARGET_IMPROVEMENT =
            PerformanceDataFacilityCalculationParameters::getTargetImprovement;

    /**
     * Sum of Primary energy/carbon for all fuels where the value is not zero
     */
    public final BiFunction<PerformanceDataFacilityInputEnergyFuelDetails, MeasurementType, BigDecimal> ACTUAL_ENERGY_CARBON =
            (energyFuelDetails, measurementType) -> {
        if(energyFuelDetails == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal sumOfStandardFuels = PerformanceDataFacilityCalculationCommonFunctionUtil.TOTAL_STANDARD_FUELS_DELIVERED_ENERGY.apply(energyFuelDetails.getStandardFuels(),
                entry -> PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL.apply(entry, measurementType));

        BigDecimal sumOfNonStandardFuels = PerformanceDataFacilityCalculationCommonFunctionUtil.TOTAL_NON_STANDARD_FUELS_DELIVERED_ENERGY.apply(energyFuelDetails.getNonStandardFuels(),
                fuel -> PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL.apply(fuel, measurementType));

        return sumOfStandardFuels.add(sumOfNonStandardFuels);
    };

    /**
     * Split by product: Sum[(BY energy/carbon intensity x Adjusted throughput) x (1 - improvement % or Interim target %)] + (BY fixed energy/carbon x (1 - improvement % or Interim target %))
     * Variable totals only: ((BY energy/carbon intensity x Adjusted throughput) (1 - improvement % or Interim target %)) + (BY fixed energy/carbon x (1 - improvement % or Interim target %))
     * Fixed only: BY fixed energy/carbon x (1 - improvement % or Interim target )
     * multiplier -> (adjust BY fixed energy/carbon to 2 x BY Fixed energy/carbon for 2 year target period)
     * improvementTarget -> improvement% or Interim target %
     */
    public final BiFunction<PerformanceDataFacilityCalculationParameters, PerformanceDataFacilityInputData, BigDecimal> TARGET_ENERGY_CARBON =
            (calculatedParameters, data) -> {
                Boolean usedReportingMechanism = calculatedParameters.getUsedReportingMechanism();
                PerformanceDataFacilityInputEnergyFuelDetails fuelDetails = data.getEnergyFuelDetails();
                PerformanceDataFacilityThroughputDetails throughputDetails = data.getThroughputDetails();

                BigDecimal improvementSubtract = BigDecimal.ONE.subtract(calculatedParameters.getTargetImprovement());

                BigDecimal fixedEnergyImprovement = calculatedParameters.getTpMultiplier().multiply(calculatedParameters.getTotalFixedEnergy(), MathContext.DECIMAL128)
                        .multiply(improvementSubtract, MathContext.DECIMAL128);

                if (calculatedParameters.getVariableEnergyType() == null) {
                    return fixedEnergyImprovement;
                }

                return switch (calculatedParameters.getVariableEnergyType()) {
                    case BY_PRODUCT -> {
                        BigDecimal productsSum = PerformanceDataFacilityCalculationCommonFunctionUtil.PRODUCT_SUM_ENERGY_ADJUSTED_THROUGHPUT
                                .apply(calculatedParameters, data);

                        yield productsSum.add(fixedEnergyImprovement);
                    }
                    case TOTALS -> {
                        BigDecimal energyCarbonIntensity = PerformanceDataFacilityCalculationCommonFunctionUtil.ENERGY_CARBON_INTENSITY
                                .apply(calculatedParameters.getBaselineVariableEnergy(), calculatedParameters.getTotalThroughput());
                        BigDecimal adjustedThroughput = PerformanceDataFacilityCalculationCommonFunctionUtil.ADJUSTED_THROUGHPUT
                                .apply(usedReportingMechanism, fuelDetails, throughputDetails.getActualThroughput());

                        yield energyCarbonIntensity.multiply(adjustedThroughput, MathContext.DECIMAL128)
                                .multiply(improvementSubtract, MathContext.DECIMAL128)
                                .add(fixedEnergyImprovement);
                    }
                };
    };

    /**
     * energy/CO2 difference = Actual TP energy/carbon - Target energy/CO2 at TP throughput
     */
    public final BinaryOperator<BigDecimal> ENERGY_CARBON_DIFFERENCE = BigDecimal::subtract;

    /**
     * (Target for end of TP + Target for previous TP) / 2
     */
    public final BiFunction<Map<TargetImprovementType, BigDecimal>, TargetPeriodType, BigDecimal> INTERIM_TARGET =
            (improvements, targetPeriodType) -> {
                Optional<Map.Entry<TargetImprovementType, BigDecimal>> entry = improvements.entrySet().stream()
                        .filter(improvement -> improvement.getKey().name().equals(targetPeriodType.name()))
                        .findFirst();
                if (entry.isPresent()) {
                    BigDecimal previous = TargetImprovementType.getTargetImprovementTypeByTargetPeriodNumber(entry.get().getKey().getTargetPeriodNumber() - 1)
                            .map(improvements::get)
                            .orElse(BigDecimal.ZERO);

                    return entry.get().getValue().add(previous)
                            .divide(BigDecimal.TWO, MathContext.DECIMAL128);
                }

                return BigDecimal.ZERO;
    };

    /**
     * TP Weighted conversion factor = [sum(Primary TP energy amount x Conversion factor)]/ Actual TP energy
     */
    public final TriFunction<PerformanceDataFacilityInputEnergyFuelDetails, BigDecimal, MeasurementType, BigDecimal> WEIGHTED_CONVERSION_FACTOR =
            (fuelDetails, actualEnergyCarbon, measurementType) -> {
        if(actualEnergyCarbon.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal sumOfStandardFuels = PerformanceDataFacilityCalculationCommonFunctionUtil.TOTAL_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getStandardFuels(),
                entry -> PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL
                        .apply(entry, measurementType)
                        .multiply(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(entry.getKey(), measurementType), MathContext.DECIMAL128));

        BigDecimal sumOfNonStandardFuels = PerformanceDataFacilityCalculationCommonFunctionUtil.TOTAL_NON_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getNonStandardFuels(),
                fuel -> PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL
                        .apply(fuel, measurementType)
                        .multiply(fuel.getConversionFactor(), MathContext.DECIMAL128));

        return sumOfStandardFuels.add(sumOfNonStandardFuels).divide(actualEnergyCarbon, MathContext.DECIMAL128);
    };

    /**
     * Energy based: Target tCO2e emitted = Target energy at TP throughput x Weighted Average conversion factor / 1000
     * Carbon based: Target tCO2e emitted = Target carbon at TP throughput
     */
    public final TriFunction<BigDecimal, BigDecimal, MeasurementType, BigDecimal> TARGET_CO2_EMISSIONS =
            (targetEnergyCarbonThroughput, weightedConversionFactor, measurementType) ->
                    switch (measurementType) {
                        case ENERGY_KWH, ENERGY_MWH, ENERGY_GJ ->
                                targetEnergyCarbonThroughput.multiply(weightedConversionFactor, MathContext.DECIMAL128)
                                        .divide(BigDecimal.valueOf(1000), MathContext.DECIMAL128);
                        case CARBON_KG, CARBON_TONNE -> targetEnergyCarbonThroughput.divide(BigDecimal.valueOf(1000), MathContext.DECIMAL128);
    };

    /**
     * Energy based: For each fuel where Total consumption (1) value is not blank, tCO2e =Total consumption x Conversion factor / 1000= tCO2e for the specific fuel
     * Carbon based: For each fuel where Total consumption (1) value is not blank, tCO2e =Total consumption/ 1000= tCO2e for the specific fuel
     */
    public final BiFunction<PerformanceDataFacilityInputEnergyFuelDetails, MeasurementType, BigDecimal> ACTUAL_CO2_EMISSIONS =
            (fuelDetails, measurementType) -> {
        if(fuelDetails == null) {
            return BigDecimal.ZERO;
        }
        return switch (measurementType) {
            case ENERGY_KWH, ENERGY_MWH, ENERGY_GJ -> {
                BigDecimal sumOfStandardFuels = PerformanceDataFacilityCalculationCommonFunctionUtil.TOTAL_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getStandardFuels(),
                        entry -> PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL
                                .apply(entry, measurementType)
                                .multiply(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(entry.getKey(), measurementType), MathContext.DECIMAL128)
                                .divide(BigDecimal.valueOf(1000), MathContext.DECIMAL128));

                BigDecimal sumOfNonStandardFuels = PerformanceDataFacilityCalculationCommonFunctionUtil.TOTAL_NON_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getNonStandardFuels(),
                        fuel -> PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL
                                .apply(fuel, measurementType)
                                .multiply(fuel.getConversionFactor(), MathContext.DECIMAL128)
                                .divide(BigDecimal.valueOf(1000), MathContext.DECIMAL128));

                yield  sumOfStandardFuels.add(sumOfNonStandardFuels);
            }
            case CARBON_KG, CARBON_TONNE -> {
                BigDecimal sumOfStandardFuels = PerformanceDataFacilityCalculationCommonFunctionUtil.TOTAL_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getStandardFuels(),
                        entry -> PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL
                                .apply(entry, measurementType)
                                .divide(BigDecimal.valueOf(1000), MathContext.DECIMAL128));

                BigDecimal sumOfNonStandardFuels = PerformanceDataFacilityCalculationCommonFunctionUtil.TOTAL_NON_STANDARD_FUELS_DELIVERED_ENERGY.apply(fuelDetails.getNonStandardFuels(),
                        fuel -> PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL
                                .apply(fuel, measurementType)
                                .divide(BigDecimal.valueOf(1000), MathContext.DECIMAL128));

                yield  sumOfStandardFuels.add(sumOfNonStandardFuels);
            }
        };
    };

    /**
     * tCO2e difference = Actual Target tCO2e minus Target Actual tCO2e
     */
    public final BinaryOperator<BigDecimal> CO2_EMISSIONS_DIFFERENCE = BigDecimal::subtract;

    /**
     * Actual improvement % = 1 - (Actual TP energy/carbon /BY energy/carbon at TP throughput)
     * If BY energy/carbon at TP throughput = 0, improvement % will be 0
     */
    public final TriFunction<BigDecimal, PerformanceDataFacilityCalculationParameters, PerformanceDataFacilityInputData, BigDecimal> ACTUAL_IMPROVEMENT =
            (actualEnergyCarbon, calculatedParameters, data) -> {
        BigDecimal baseEnergyCarbonThroughput = PerformanceDataFacilityCalculationCommonFunctionUtil.BASE_ENERGY_CARBON_THROUGHPUT
                .apply(calculatedParameters, data);
        BigDecimal division = baseEnergyCarbonThroughput.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ONE
                : actualEnergyCarbon.divide(baseEnergyCarbonThroughput, MathContext.DECIMAL128);

        return BigDecimal.ONE.subtract(division);
    };

    /**
     * If Energy difference = zero or negative number=>Target met
     * If Energy difference = positive =>Target not met
     */
    public final Function<BigDecimal, PerformanceDataFacilityTargetPeriodResultType> TARGET_PERIOD_RESULT_TYPE =
            energyCarbonDifference -> energyCarbonDifference.compareTo(BigDecimal.ZERO) <= 0
                    ? PerformanceDataFacilityTargetPeriodResultType.TARGET_MET
                    : PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET;

    /**
     * tCO2e difference IF it is a negative
     */
    public final UnaryOperator<BigDecimal> SURPLUS_GAINED =
            co2EmissionsDifference -> co2EmissionsDifference.compareTo(BigDecimal.ZERO) < 0
                    ? co2EmissionsDifference.abs().setScale(0, RoundingMode.DOWN)
                    : BigDecimal.ZERO;

    /**
     * tCO2e difference IF it is a positive
     */
    public final UnaryOperator<BigDecimal> BUY_OUT_REQUIRED =
            co2EmissionsDifference -> co2EmissionsDifference.compareTo(BigDecimal.ZERO) > 0
                    ? co2EmissionsDifference.setScale(0, RoundingMode.UP)
                    : BigDecimal.ZERO;
}
