package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.transform;

import static uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityProductVariableEnergyData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils.PerformanceDataFacilityCalculationCommonFunctionUtil;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityUploadCsvData;

@UtilityClass
public class PerformanceDataFacilityProcessingInputDataMapper {

    public PerformanceDataFacilityInputData toPerformanceDataFacilityInputData(final PerformanceDataFacilityUploadCsvData csvData,
                                                                               final PerformanceDataFacilityCalculationParameters calculationParameters) {
        boolean isSrmValid = (Boolean.TRUE.equals(calculationParameters.getUsedReportingMechanism()) && csvData.getElectricitySuppliedFromCHP() != null)
                || Boolean.FALSE.equals(calculationParameters.getUsedReportingMechanism());

        PerformanceDataFacilityInputEnergyFuelDetails fuelDetails =
                toPerformanceDataFacilityInputEnergyFuelDetails(csvData, calculationParameters);
        PerformanceDataFacilityThroughputDetails throughputDetails =
                toPerformanceDataFacilityThroughputDetails(csvData, calculationParameters, fuelDetails, isSrmValid);

        PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(fuelDetails)
                .throughputDetails(throughputDetails)
                .build();

        // Calculate totalTargetVariableEnergy
        BigDecimal totalTargetVariableEnergy = BigDecimal.ZERO;
        if(isSrmValid && (
                (calculationParameters.getVariableEnergyType() == VariableEnergyDepictionType.TOTALS && performanceData.getThroughputDetails().getActualThroughput() != null)
                || (calculationParameters.getVariableEnergyType() == VariableEnergyDepictionType.BY_PRODUCT)
                || (calculationParameters.getVariableEnergyType() == null)
        )) {
            totalTargetVariableEnergy = PerformanceDataFacilityCalculationCommonFunctionUtil
                    .TOTAL_TARGET_VARIABLE_ENERGY.apply(calculationParameters, performanceData).setScale(7, RoundingMode.HALF_UP);
        }
        performanceData.getThroughputDetails().setTotalTargetVariableEnergy(totalTargetVariableEnergy);

        return performanceData;
    }

    private PerformanceDataFacilityInputEnergyFuelDetails toPerformanceDataFacilityInputEnergyFuelDetails(final PerformanceDataFacilityUploadCsvData csvData,
                                                                                                          final PerformanceDataFacilityCalculationParameters calculationParameters) {
        final MeasurementType measurementType = calculationParameters.getMeasurementType();

        // StandardFuels
        Map<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption> standardFuels =
                new EnumMap<>(PerformanceDataFacilityFixedConversionFactor.class);
        addToStandardFuels(csvData.getGridElectricity(), GRID_ELECTRICITY, measurementType, standardFuels);
        addToStandardFuels(csvData.getNonGridElectricity(), NON_GRID_ELECTRICITY, measurementType, standardFuels);
        addToStandardFuels(csvData.getNaturalGas(), NATURAL_GAS, measurementType, standardFuels);
        addToStandardFuels(csvData.getLpg(), LPG, measurementType, standardFuels);
        addToStandardFuels(csvData.getGasDieselOil(), GAS_DIESEL_OIL, measurementType, standardFuels);
        addToStandardFuels(csvData.getFuelOil(), FUEL_OIL, measurementType, standardFuels);
        addToStandardFuels(csvData.getKerosene(), KEROSENE, measurementType, standardFuels);
        addToStandardFuels(csvData.getCoal(), COAL, measurementType, standardFuels);
        addToStandardFuels(csvData.getCoke(), COKE, measurementType, standardFuels);
        addToStandardFuels(csvData.getPetrol(), PETROL, measurementType, standardFuels);
        addToStandardFuels(csvData.getNitrogen(), NITROGEN_COOLING, measurementType, standardFuels);
        addToStandardFuels(csvData.getCarbonDioxide(), CARBON_DIOXIDE_COOLING, measurementType, standardFuels);
        addToStandardFuels(csvData.getEthane(), ETHANE, measurementType, standardFuels);
        addToStandardFuels(csvData.getNaphtha(), NAPHTHA, measurementType, standardFuels);
        addToStandardFuels(csvData.getPetroleumCoke(), PETROLEUM_COKE, measurementType, standardFuels);
        addToStandardFuels(csvData.getRefineryGas(), REFINERY_GAS, measurementType, standardFuels);

        // NonStandardFuels
        List<PerformanceDataFacilityNonStandardFuel> nonStandardFuels = new ArrayList<>();
        addToNonStandardFuels(csvData.getOtherFuelName1(), csvData.getOtherFuelConversionFactor1(), csvData.getOtherFuelAmount1(), measurementType, nonStandardFuels);
        addToNonStandardFuels(csvData.getOtherFuelName2(), csvData.getOtherFuelConversionFactor2(), csvData.getOtherFuelAmount2(), measurementType, nonStandardFuels);
        addToNonStandardFuels(csvData.getOtherFuelName3(), csvData.getOtherFuelConversionFactor3(), csvData.getOtherFuelAmount3(), measurementType, nonStandardFuels);
        addToNonStandardFuels(csvData.getOtherFuelName4(), csvData.getOtherFuelConversionFactor4(), csvData.getOtherFuelAmount4(), measurementType, nonStandardFuels);
        addToNonStandardFuels(csvData.getOtherFuelName5(), csvData.getOtherFuelConversionFactor5(), csvData.getOtherFuelAmount5(), measurementType, nonStandardFuels);
        addToNonStandardFuels(csvData.getOtherFuelName6(), csvData.getOtherFuelConversionFactor6(), csvData.getOtherFuelAmount6(), measurementType, nonStandardFuels);
        addToNonStandardFuels(csvData.getOtherFuelName7(), csvData.getOtherFuelConversionFactor7(), csvData.getOtherFuelAmount7(), measurementType, nonStandardFuels);
        addToNonStandardFuels(csvData.getOtherFuelName8(), csvData.getOtherFuelConversionFactor8(), csvData.getOtherFuelAmount8(), measurementType, nonStandardFuels);
        addToNonStandardFuels(csvData.getOtherFuelName9(), csvData.getOtherFuelConversionFactor9(), csvData.getOtherFuelAmount9(), measurementType, nonStandardFuels);
        addToNonStandardFuels(csvData.getOtherFuelName10(), csvData.getOtherFuelConversionFactor10(), csvData.getOtherFuelAmount10(), measurementType, nonStandardFuels);

        // ElectricitySuppliedFromCHP
        BigDecimal electricitySuppliedFromCHP = csvData.getElectricitySuppliedFromCHP();

        // ThroughputAdjustmentFactor
        BigDecimal throughputAdjustmentFactor = Boolean.TRUE.equals(calculationParameters.getUsedReportingMechanism()) && electricitySuppliedFromCHP != null
                ? PerformanceDataFacilityCalculationCommonFunctionUtil.THROUGHPUT_ADJUSTMENT_FACTOR
                    .apply(standardFuels, electricitySuppliedFromCHP).setScale(7, RoundingMode.HALF_UP)
                : null;


        return PerformanceDataFacilityInputEnergyFuelDetails.builder()
                .standardFuels(standardFuels)
                .nonStandardFuels(nonStandardFuels)
                .atLeastSeventyPercentEnergyUsed(csvData.getAtLeastSeventyPercentEnergyUsed())
                .electricitySuppliedFromCHP(electricitySuppliedFromCHP)
                .throughputAdjustmentFactor(throughputAdjustmentFactor)
                .build();
    }

    private PerformanceDataFacilityThroughputDetails toPerformanceDataFacilityThroughputDetails(final PerformanceDataFacilityUploadCsvData csvData,
                                                                                                final PerformanceDataFacilityCalculationParameters calculationParameters,
                                                                                                final PerformanceDataFacilityInputEnergyFuelDetails fuelDetails,
                                                                                                boolean isSrmValid) {
        // ActualThroughput
        BigDecimal actualThroughput = csvData.getActualThroughput();

        // TargetImprovement
        BigDecimal targetImprovement = VariableEnergyDepictionType.BY_PRODUCT == calculationParameters.getVariableEnergyType()
                ? null
                : calculationParameters.getTargetImprovement().setScale(7, RoundingMode.HALF_UP);

        // AdjustedThroughput
        BigDecimal adjustedThroughput = VariableEnergyDepictionType.BY_PRODUCT == calculationParameters.getVariableEnergyType() || !isSrmValid || actualThroughput == null
                ? null
                : PerformanceDataFacilityCalculationCommonFunctionUtil.ADJUSTED_THROUGHPUT
                    .apply(calculationParameters.getUsedReportingMechanism(), fuelDetails, actualThroughput)
                    .setScale(7, RoundingMode.HALF_UP);

        // Add products
        List<PerformanceDataFacilityProductVariableEnergyData> variableEnergyConsumptionDataByProduct = new ArrayList<>();
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName1(), csvData.getProductActualThroughput1(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName2(), csvData.getProductActualThroughput2(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName3(), csvData.getProductActualThroughput3(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName4(), csvData.getProductActualThroughput4(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName5(), csvData.getProductActualThroughput5(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName6(), csvData.getProductActualThroughput6(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName7(), csvData.getProductActualThroughput7(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName8(), csvData.getProductActualThroughput8(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName9(), csvData.getProductActualThroughput9(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName10(), csvData.getProductActualThroughput10(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName11(), csvData.getProductActualThroughput11(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName12(), csvData.getProductActualThroughput12(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName13(), csvData.getProductActualThroughput13(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName14(), csvData.getProductActualThroughput14(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName15(), csvData.getProductActualThroughput15(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName16(), csvData.getProductActualThroughput16(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName17(), csvData.getProductActualThroughput17(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName18(), csvData.getProductActualThroughput18(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName19(), csvData.getProductActualThroughput19(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);
        addToVariableEnergyConsumptionData(isSrmValid, csvData.getProductName20(), csvData.getProductActualThroughput20(), calculationParameters, fuelDetails, variableEnergyConsumptionDataByProduct);

        // Initialize products that not exist in CSV
        addOriginalProductsNotInCsvToVariableEnergyConsumptionData(calculationParameters, variableEnergyConsumptionDataByProduct);

        return PerformanceDataFacilityThroughputDetails.builder()
                .actualThroughput(actualThroughput)
                .targetImprovement(targetImprovement)
                .adjustedThroughput(adjustedThroughput)
                .variableEnergyConsumptionDataByProduct(variableEnergyConsumptionDataByProduct)
                .build();
    }

    private void addToStandardFuels(BigDecimal value, PerformanceDataFacilityFixedConversionFactor fuelType, MeasurementType measurementType,
                               Map<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption> standardFuels) {
        Optional.ofNullable(value)
                .ifPresent(v -> standardFuels.put(fuelType, convertToStandardFuel(fuelType, v, measurementType)));
    }

    private PerformanceDataFacilityFuelEnergyConsumption convertToStandardFuel(PerformanceDataFacilityFixedConversionFactor factor,
                                                                               BigDecimal deliveredEnergy, MeasurementType measurementType) {
    	    	
        Map.Entry<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption> entry = Map
                .entry(factor, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(deliveredEnergy).build());

        BigDecimal primaryEnergy = PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL
                .apply(entry, measurementType).setScale(7, RoundingMode.HALF_UP);

        return PerformanceDataFacilityFuelEnergyConsumption.builder()
                .deliveredEnergy(deliveredEnergy)
                .primaryEnergy(primaryEnergy)
                .build();
    }

    private void addToNonStandardFuels(String otherFuelName, BigDecimal otherFuelConversionFactor, BigDecimal otherFuelAmount,
                                          MeasurementType measurementType, List<PerformanceDataFacilityNonStandardFuel> nonStandardFuels) {
        boolean hasNonStandardFuelData = !ObjectUtils.isEmpty(otherFuelName) || !ObjectUtils.isEmpty(otherFuelConversionFactor)
                || !ObjectUtils.isEmpty(otherFuelAmount);
        if(!hasNonStandardFuelData) {
        	return;
        }
        
        PerformanceDataFacilityNonStandardFuel fuel = PerformanceDataFacilityNonStandardFuel.builder()
                .name(otherFuelName)
                .conversionFactor(otherFuelConversionFactor)
                .deliveredEnergy(otherFuelAmount)
                .build();

        // PrimaryEnergy
        BigDecimal primaryEnergy = !ObjectUtils.isEmpty(otherFuelConversionFactor) && !ObjectUtils.isEmpty(otherFuelAmount)
                ? PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL
                    .apply(fuel, measurementType).setScale(7, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        fuel.setPrimaryEnergy(primaryEnergy);

        nonStandardFuels.add(fuel);
        
    }

    private void addToVariableEnergyConsumptionData(boolean isSrmValid, String productName, BigDecimal productActualThroughput,
                                                    final PerformanceDataFacilityCalculationParameters calculationParameters,
                                                    final PerformanceDataFacilityInputEnergyFuelDetails fuelDetails,
                                                    List<PerformanceDataFacilityProductVariableEnergyData> variableEnergyConsumptionDataByProduct) {
        boolean hasAnyProductAttribute = !ObjectUtils.isEmpty(productName) || !ObjectUtils.isEmpty(productActualThroughput);
        if(!hasAnyProductAttribute) {
        	return;
        }
        
        Optional<ProductVariableEnergyConsumptionData> originalProductFound = calculationParameters.getVariableEnergyConsumptionDataByProduct().stream()
                .filter(p -> ObjectUtils.compare(p.getProductName(), productName) == 0)
                .findFirst();

        originalProductFound.ifPresentOrElse(
                originalProduct -> {
                    // TargetImprovement
                    BigDecimal targetImprovement = PerformanceDataFacilityCalculationCommonFunctionUtil
                            .PRODUCT_TARGET_IMPROVEMENT.apply(calculationParameters, originalProduct).setScale(7, RoundingMode.HALF_UP);

                    // AdjustedThroughput
                    BigDecimal actualThroughput = Optional.ofNullable(productActualThroughput).orElse(BigDecimal.ZERO);
                    BigDecimal adjustedThroughput = isSrmValid
                            ? PerformanceDataFacilityCalculationCommonFunctionUtil.ADJUSTED_THROUGHPUT
                                .apply(calculationParameters.getUsedReportingMechanism(), fuelDetails, actualThroughput)
                                .setScale(7, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    BigDecimal targetEnergy = isSrmValid
                            ? PerformanceDataFacilityCalculationCommonFunctionUtil.PRODUCT_ENERGY_
                                    .apply(calculationParameters, originalProduct, fuelDetails, actualThroughput)
                                    .setScale(7, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;

                    // Add product
                    PerformanceDataFacilityProductVariableEnergyData product = PerformanceDataFacilityProductVariableEnergyData.builder()
                            .productName(productName)
                            .targetImprovement(targetImprovement)
                            .actualThroughput(productActualThroughput)
                            .adjustedThroughput(adjustedThroughput)
                            .targetEnergy(targetEnergy)
                            .build();
                    variableEnergyConsumptionDataByProduct.add(product);
                },
                () -> variableEnergyConsumptionDataByProduct.add(PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName(productName)
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(productActualThroughput)
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build()));
        
    }

    private void addOriginalProductsNotInCsvToVariableEnergyConsumptionData(final PerformanceDataFacilityCalculationParameters calculationParameters,
                                                                            List<PerformanceDataFacilityProductVariableEnergyData> variableEnergyConsumptionDataByProduct) {
        calculationParameters.getVariableEnergyConsumptionDataByProduct().forEach(originalProduct -> {
            if(variableEnergyConsumptionDataByProduct.stream()
                    .noneMatch(p -> ObjectUtils.compare(p.getProductName(), originalProduct.getProductName()) == 0)) {
                BigDecimal actualThroughput = BigDecimal.ZERO;
                BigDecimal targetImprovement = PerformanceDataFacilityCalculationCommonFunctionUtil
                        .PRODUCT_TARGET_IMPROVEMENT.apply(calculationParameters, originalProduct).setScale(7, RoundingMode.HALF_UP);
                BigDecimal adjustedThroughput = BigDecimal.ZERO;
                BigDecimal targetEnergy = BigDecimal.ZERO;

                variableEnergyConsumptionDataByProduct.add(PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName(originalProduct.getProductName())
                        .targetImprovement(targetImprovement)
                        .actualThroughput(actualThroughput)
                        .adjustedThroughput(adjustedThroughput)
                        .targetEnergy(targetEnergy)
                        .build());
            }
        });
    }
}
