package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityProductVariableEnergyData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityInputDataValidator {

    private final DataValidator<PerformanceDataFacilityInputData> performanceDataValidator;

    public List<BusinessValidationResult> validateData(final PerformanceDataFacilityInputData performanceData,
                                                       final PerformanceDataFacilityCalculationParameters calculationParameters) {
        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate data
        validationResults.add(validateData(performanceData));

        // Validate fuels
        validationResults.addAll(validateFuels(performanceData.getEnergyFuelDetails(), calculationParameters));

        // Validate throughput details
        validationResults.addAll(validateThroughputDetails(performanceData.getThroughputDetails(), calculationParameters));

        return validationResults;
    }

    private BusinessValidationResult validateData(final PerformanceDataFacilityInputData performanceData) {
        List<PerformanceDataFacilityViolation> violations = new ArrayList<>();
        performanceDataValidator.validate(performanceData)
                .map(businessViolation ->
                        new PerformanceDataFacilityViolation(PerformanceDataFacilityInputData.class.getName(),
                                PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.INVALID_PERFORMANCE_DATA,
                                businessViolation.getData()))
                .ifPresent(violations::add);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    private List<BusinessValidationResult> validateFuels(final PerformanceDataFacilityInputEnergyFuelDetails energyFuelDetails,
                                                   final PerformanceDataFacilityCalculationParameters calculationParameters) {
        if(ObjectUtils.isEmpty(energyFuelDetails)) {
            return List.of(BusinessValidationResult.valid());
        }

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate SRM
        String srmMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_SRM_DATA.getMessage();

        validationResults.add(Boolean.TRUE.equals(calculationParameters.getUsedReportingMechanism())
                ? PerformanceDataFacilityValidationHelper.validateSRM(srmMessage).process(energyFuelDetails)
                : PerformanceDataFacilityValidationHelper.validateNotSRM(srmMessage).process(energyFuelDetails)
        );

        // Validate CHP
        String chpMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_CHP_DATA.getMessage();

        validationResults.add(PerformanceDataFacilityValidationHelper.validateCHP(chpMessage).process(energyFuelDetails));

        return validationResults;
    }

    private List<BusinessValidationResult> validateThroughputDetails(final PerformanceDataFacilityThroughputDetails throughputDetails,
                                                                     final PerformanceDataFacilityCalculationParameters calculationParameters) {
        if(ObjectUtils.isEmpty(throughputDetails)) {
            return List.of(BusinessValidationResult.valid());
        }

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate Variable energy type
        String errorMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_VARIABLE_ENERGY_DATA.getMessage();

        if(VariableEnergyDepictionType.BY_PRODUCT == calculationParameters.getVariableEnergyType()) {
            validationResults.add(PerformanceDataFacilityValidationHelper.validate(d -> ObjectUtils.isEmpty(d.getActualThroughput()), errorMessage)
                    .and(PerformanceDataFacilityValidationHelper.validate(d -> ObjectUtils.isEmpty(d.getTargetImprovement()), errorMessage))
                    .and(PerformanceDataFacilityValidationHelper.validate(d -> ObjectUtils.isEmpty(d.getAdjustedThroughput()), errorMessage))
                    .and(PerformanceDataFacilityValidationHelper.validate(d -> !d.getVariableEnergyConsumptionDataByProduct().isEmpty(), errorMessage))
                    .process(throughputDetails));
        }
        else {
            validationResults.add(PerformanceDataFacilityValidationHelper.validate(d -> !ObjectUtils.isEmpty(d.getActualThroughput()), errorMessage)
                    .and(PerformanceDataFacilityValidationHelper.validate(d -> !ObjectUtils.isEmpty(d.getTargetImprovement()), errorMessage))
                    .and(PerformanceDataFacilityValidationHelper.validate(d -> !ObjectUtils.isEmpty(d.getAdjustedThroughput()), errorMessage))
                    .and(PerformanceDataFacilityValidationHelper.validate(d -> d.getVariableEnergyConsumptionDataByProduct().isEmpty(), errorMessage))
                    .process(throughputDetails));
        }

        // Validate Products
        if(!throughputDetails.getVariableEnergyConsumptionDataByProduct().isEmpty()) {
            Set<String> originalProducts = calculationParameters.getVariableEnergyConsumptionDataByProduct().stream()
                    .map(ProductVariableEnergyConsumptionData::getProductName)
                    .collect(Collectors.toSet());
            Set<String> actualProducts = throughputDetails.getVariableEnergyConsumptionDataByProduct().stream()
                    .map(PerformanceDataFacilityProductVariableEnergyData::getProductName)
                    .collect(Collectors.toSet());

            // Should include only products from original
            String productMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                    .INVALID_PRODUCTS.getMessage();
            validationResults.add(ValidatorHelper.validateSetsEquals(productMessage)
                    .process(Collections.singleton(originalProducts), Collections.singleton(actualProducts)));

            // Should not include duplicate products
            Set<String> productNames = throughputDetails.getVariableEnergyConsumptionDataByProduct().stream()
                    .map(PerformanceDataFacilityProductVariableEnergyData::getProductName).collect(Collectors.toSet());
            if(throughputDetails.getVariableEnergyConsumptionDataByProduct().size() != productNames.size()) {
                validationResults.add(BusinessValidationResult.invalid(List.of(
                        new BusinessViolation("", PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                                .DUPLICATE_PRODUCTS_EXISTS.getMessage())
                )));
            }
        }

        return validationResults;
    }
}
