package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform.PerformanceDataFacilityCalculationMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class PerformanceDataFacilityDigitalFormCalculatedDataValidator {

    public List<BusinessValidationResult> validateCalculatedData(final PerformanceDataFacilityInputData performanceData,
                                                                 final PerformanceDataFacilityCalculationParameters calculationParameters) {

        final PerformanceDataFacilityCalculatedResults inputResults = performanceData.getCalculatedResults();
        PerformanceDataFacilityCalculatedResults expectedResults = PerformanceDataFacilityCalculationMapper
                .toPerformanceDataFacilityCalculatedResults(calculationParameters, performanceData);

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Actual target period energy or carbon
        String actualEnergyCarbonMessage = constructErrorMessage("actualEnergyCarbon", expectedResults.getActualEnergyCarbon());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(actualEnergyCarbonMessage)
                .process(expectedResults.getActualEnergyCarbon(), inputResults.getActualEnergyCarbon()));

        // Target energy/CO2
        String targetEnergyCarbonMessage = constructErrorMessage("targetEnergyCarbon", expectedResults.getTargetEnergyCarbon());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(targetEnergyCarbonMessage)
                .process(expectedResults.getTargetEnergyCarbon(), inputResults.getTargetEnergyCarbon()));

        // Energy/carbon difference
        String energyCarbonDifferenceMessage = constructErrorMessage("energyCarbonDifference", expectedResults.getEnergyCarbonDifference());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(energyCarbonDifferenceMessage)
                .process(expectedResults.getEnergyCarbonDifference(), inputResults.getEnergyCarbonDifference()));

        // Improvement target
        String targetImprovementMessage = constructErrorMessage("targetImprovement", expectedResults.getTargetImprovement());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(targetImprovementMessage)
                .process(expectedResults.getTargetImprovement(), inputResults.getTargetImprovement()));

        // Target period weighted conversion factor
        String weightedConversionFactorMessage = constructErrorMessage("weightedConversionFactor", expectedResults.getWeightedConversionFactor());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(weightedConversionFactorMessage)
                .process(expectedResults.getWeightedConversionFactor(), inputResults.getWeightedConversionFactor()));

        // Target tCO2e emitted
        String targetCo2EmissionsMessage = constructErrorMessage("targetCo2Emissions", expectedResults.getTargetCo2Emissions());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(targetCo2EmissionsMessage)
                .process(expectedResults.getTargetCo2Emissions(), inputResults.getTargetCo2Emissions()));

        // Actual tCO2e emitted
        String actualCo2EmissionsMessage = constructErrorMessage("actualCo2Emissions", expectedResults.getActualCo2Emissions());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(actualCo2EmissionsMessage)
                .process(expectedResults.getActualCo2Emissions(), inputResults.getActualCo2Emissions()));

        // tCO2e difference
        String co2EmissionsDifferenceMessage = constructErrorMessage("co2EmissionsDifference", expectedResults.getCo2EmissionsDifference());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(co2EmissionsDifferenceMessage)
                .process(expectedResults.getCo2EmissionsDifference(), inputResults.getCo2EmissionsDifference()));

        // Improvement % achieved
        String actualImprovementMessage = constructErrorMessage("actualImprovement", expectedResults.getActualImprovement());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(actualImprovementMessage)
                .process(expectedResults.getActualImprovement(), inputResults.getActualImprovement()));

        // Target period result
        String targetPeriodResultTypeMessage = constructErrorMessage("targetPeriodResultType", expectedResults.getTargetPeriodResultType());
        validationResults.add(ValidatorHelper.validateEquals(targetPeriodResultTypeMessage)
                .process(expectedResults.getTargetPeriodResultType(), inputResults.getTargetPeriodResultType()));

        // Total surplus gained
        String surplusGainedMessage = constructErrorMessage("surplusGained", expectedResults.getSurplusGained());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(surplusGainedMessage)
                .process(expectedResults.getSurplusGained(), inputResults.getSurplusGained()));

        // Total buy-out required
        String buyOutRequiredMessage = constructErrorMessage("buyOutRequired", expectedResults.getBuyOutRequired());
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(buyOutRequiredMessage)
                .process(expectedResults.getBuyOutRequired(), inputResults.getBuyOutRequired()));

        return validationResults;
    }

    private String constructErrorMessage(String field, Object expected) {
        String errorMessage = PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_CALCULATED_RESULT_DATA.getMessage() + " %s: %s";

        return String.format(errorMessage, field, expected);
    }
}
