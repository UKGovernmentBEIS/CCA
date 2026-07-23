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
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
                                transformViolationData(businessViolation.getData())))
                .ifPresent(violations::add);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
    
	private Object[] transformViolationData(Object[] data) {
		return Arrays.stream(data)
				.map(value -> value instanceof String stringValue ? transformViolationPath(stringValue) : value)
				.toArray();
	}
    
    private String transformViolationPath(String path) {
    	final String LIST_INDEX_PATTERN = "(energyFuelDetails\\.nonStandardFuels|throughputDetails\\.variableEnergyConsumptionDataByProduct)\\[(\\d+)]";
        Pattern pattern = Pattern.compile(LIST_INDEX_PATTERN);

        Matcher matcher = pattern.matcher(path);

        if (matcher.find()) {
        	int index = Integer.parseInt(matcher.group(2)) + 1;

            String replacement = switch (matcher.group(1)) {
                case "energyFuelDetails.nonStandardFuels" -> "energyFuelDetails.otherFuel" + index;
                case "throughputDetails.variableEnergyConsumptionDataByProduct" -> "throughputDetails.product" + index;
                default -> matcher.group(0);
            };
                
            return matcher.replaceFirst(Matcher.quoteReplacement(replacement));
        }

        return path;
    }

    private List<BusinessValidationResult> validateFuels(final PerformanceDataFacilityInputEnergyFuelDetails energyFuelDetails,
                                                   final PerformanceDataFacilityCalculationParameters calculationParameters) {
        if(ObjectUtils.isEmpty(energyFuelDetails)) {
            return List.of(BusinessValidationResult.valid());
        }

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate SRM
        String srmMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .SRM_DATA_DOES_NOT_MATCH_USE_SRM_SELECTION.getMessage();

        validationResults.add(Boolean.TRUE.equals(calculationParameters.getUsedReportingMechanism())
                ? PerformanceDataFacilityValidationHelper.validateSRM(srmMessage).process(energyFuelDetails)
                : PerformanceDataFacilityValidationHelper.validateNotSRM(srmMessage).process(energyFuelDetails)
        );

        // Validate CHP
        if (Boolean.TRUE.equals(calculationParameters.getUsedReportingMechanism())) {
	        String chpMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
	                .INVALID_CHP_DATA.getMessage();
	
	        validationResults.add(PerformanceDataFacilityValidationHelper.validateCHP(chpMessage).process(energyFuelDetails));
        }

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
                .VARIABLE_ENERGY_DATA_DOES_NOT_MATCH_TYPE.getMessage();

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
        if(VariableEnergyDepictionType.BY_PRODUCT.equals(calculationParameters.getVariableEnergyType()) 
        		&& !throughputDetails.getVariableEnergyConsumptionDataByProduct().isEmpty()) {
            Set<String> originalProducts = calculationParameters.getVariableEnergyConsumptionDataByProduct().stream()
                    .map(ProductVariableEnergyConsumptionData::getProductName)
                    .collect(Collectors.toSet());
            Set<String> actualProducts = throughputDetails.getVariableEnergyConsumptionDataByProduct().stream()
                    .map(PerformanceDataFacilityProductVariableEnergyData::getProductName)
                    .collect(Collectors.toSet());

            // Should include only products from original
            String productMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                    .PRODUCTS_DO_NOT_MATCH_UNDERLYING_AGREEMENT.getMessage();
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
