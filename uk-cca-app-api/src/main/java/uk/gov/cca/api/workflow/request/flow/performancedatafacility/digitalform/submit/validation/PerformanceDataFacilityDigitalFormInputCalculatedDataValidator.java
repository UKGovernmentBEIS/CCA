package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityProductVariableEnergyData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils.PerformanceDataFacilityCalculationCommonFunctionUtil;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PerformanceDataFacilityDigitalFormInputCalculatedDataValidator {

    private static final String ERROR_MESSAGE = "%s %s: %s";

    public List<BusinessValidationResult> validateInputCalculatedData(final PerformanceDataFacilityInputData performanceData,
                                                       final PerformanceDataFacilityCalculationParameters calculationParameters) {
        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate fuels
        validationResults.addAll(validateFuels(performanceData.getEnergyFuelDetails(), calculationParameters));

        // Validate throughput details
        validationResults.addAll(validateThroughputDetails(performanceData, calculationParameters));

        return validationResults;
    }

    private List<BusinessValidationResult> validateFuels(final PerformanceDataFacilityInputEnergyFuelDetails energyFuelDetails,
                                                         final PerformanceDataFacilityCalculationParameters calculationParameters) {
        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate Primary energy
        String primaryErrorMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_PRIMARY_ENERGY_DATA.getMessage();

        energyFuelDetails.getStandardFuels().entrySet().forEach(entry -> {
            BigDecimal expected = PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL
                    .apply(entry, calculationParameters.getMeasurementType()).setScale(7, RoundingMode.HALF_UP);

            validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(ERROR_MESSAGE, primaryErrorMessage, entry.getKey().name(), expected))
                    .process(expected, entry.getValue().getPrimaryEnergy()));
        });

        energyFuelDetails.getNonStandardFuels().forEach(fuel -> {
            BigDecimal expected = PerformanceDataFacilityCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL
                    .apply(fuel, calculationParameters.getMeasurementType()).setScale(7, RoundingMode.HALF_UP);

            validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(ERROR_MESSAGE, primaryErrorMessage, fuel.getName(), expected))
                    .process(expected, fuel.getPrimaryEnergy()));
        });

        // Validate Throughput Adjustment Factor
        if(energyFuelDetails.getThroughputAdjustmentFactor() != null) {
            String throughputAdjustmentFactorErrorMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                    .INVALID_FUELS_THROUGHPUT_ADJUSTMENT_FACTOR_DATA.getMessage() + " : %s";
            BigDecimal expectedThroughputAdjustmentFactor = PerformanceDataFacilityCalculationCommonFunctionUtil.THROUGHPUT_ADJUSTMENT_FACTOR
                    .apply(energyFuelDetails.getStandardFuels(), energyFuelDetails.getElectricitySuppliedFromCHP()).setScale(7, RoundingMode.HALF_UP);

            validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(throughputAdjustmentFactorErrorMessage, expectedThroughputAdjustmentFactor))
                    .process(expectedThroughputAdjustmentFactor, energyFuelDetails.getThroughputAdjustmentFactor()));
        }

        return validationResults;
    }

    private List<BusinessValidationResult> validateThroughputDetails(final PerformanceDataFacilityInputData performanceData,
                                                                     final PerformanceDataFacilityCalculationParameters calculationParameters) {
        final PerformanceDataFacilityThroughputDetails throughputDetails = performanceData.getThroughputDetails();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        String variableEnergyMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_VARIABLE_ENERGY_DATA.getMessage();

        // Validate target improvement
        if(throughputDetails.getTargetImprovement() != null) {
            BigDecimal targetImprovement = calculationParameters.getTargetImprovement().setScale(7, RoundingMode.HALF_UP);

            validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(ERROR_MESSAGE, variableEnergyMessage, "targetImprovement", targetImprovement))
                    .process(targetImprovement, throughputDetails.getTargetImprovement()));
        }

        // Validate adjusted throughput
        if(throughputDetails.getAdjustedThroughput() != null) {
            BigDecimal adjustedThroughput = PerformanceDataFacilityCalculationCommonFunctionUtil.ADJUSTED_THROUGHPUT
                    .apply(calculationParameters.getUsedReportingMechanism(), performanceData.getEnergyFuelDetails(), throughputDetails.getActualThroughput())
                    .setScale(7, RoundingMode.HALF_UP);

            validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(ERROR_MESSAGE, variableEnergyMessage, "adjustedThroughput", adjustedThroughput))
                    .process(adjustedThroughput, throughputDetails.getAdjustedThroughput()));
        }

        // Validate total target variable energy
        BigDecimal totalTargetVariableEnergy = PerformanceDataFacilityCalculationCommonFunctionUtil
                .TOTAL_TARGET_VARIABLE_ENERGY.apply(calculationParameters, performanceData).setScale(7, RoundingMode.HALF_UP);

        validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(ERROR_MESSAGE, variableEnergyMessage, "totalTargetVariableEnergy", totalTargetVariableEnergy))
                .process(totalTargetVariableEnergy, throughputDetails.getTotalTargetVariableEnergy()));

        // Validate Products
        if(!throughputDetails.getVariableEnergyConsumptionDataByProduct().isEmpty()) {
            // For each product
            throughputDetails.getVariableEnergyConsumptionDataByProduct().forEach(product -> {
                Optional<ProductVariableEnergyConsumptionData> originalProductFound = calculationParameters.getVariableEnergyConsumptionDataByProduct().stream()
                        .filter(p -> p.getProductName().equals(product.getProductName()))
                        .findFirst();

                originalProductFound.ifPresent(productVariableEnergyConsumptionData ->
                        validateProduct(calculationParameters, performanceData, productVariableEnergyConsumptionData, product, validationResults));
            });
        }

        return validationResults;
    }

    private void validateProduct(final PerformanceDataFacilityCalculationParameters calculationParameters,
                                 final PerformanceDataFacilityInputData performanceData,
                                 final ProductVariableEnergyConsumptionData originalProduct,
                                 final PerformanceDataFacilityProductVariableEnergyData product,
                                 List<BusinessValidationResult> validationResults) {
        String productMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_PRODUCT_CALCULATED_DATA.getMessage() + " " + product.getProductName();

        // Validate target improvement
        BigDecimal targetImprovement = PerformanceDataFacilityCalculationCommonFunctionUtil
                .PRODUCT_TARGET_IMPROVEMENT.apply(calculationParameters, originalProduct).setScale(7, RoundingMode.HALF_UP);

        validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(ERROR_MESSAGE, productMessage, "targetImprovement", targetImprovement))
                .process(targetImprovement, product.getTargetImprovement()));

        // Validate adjusted throughput
        BigDecimal adjustedThroughput = PerformanceDataFacilityCalculationCommonFunctionUtil.ADJUSTED_THROUGHPUT
                .apply(calculationParameters.getUsedReportingMechanism(), performanceData.getEnergyFuelDetails(), product.getActualThroughput())
                .setScale(7, RoundingMode.HALF_UP);

        validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(ERROR_MESSAGE, productMessage, "adjustedThroughput", adjustedThroughput))
                .process(adjustedThroughput, product.getAdjustedThroughput()));

        // Validate target energy
        BigDecimal targetEnergy = PerformanceDataFacilityCalculationCommonFunctionUtil.PRODUCT_ENERGY
                .apply(calculationParameters, originalProduct, performanceData)
                .setScale(7, RoundingMode.HALF_UP);

        validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(ERROR_MESSAGE, productMessage, "targetEnergy", targetEnergy))
                .process(targetEnergy, product.getTargetEnergy()));
    }
}
