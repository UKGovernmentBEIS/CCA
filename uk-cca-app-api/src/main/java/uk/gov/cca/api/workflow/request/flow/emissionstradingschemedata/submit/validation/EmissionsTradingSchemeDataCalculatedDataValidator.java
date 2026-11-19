package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.validation.EmissionsTradingSchemeDataViolation;
import uk.gov.cca.api.workflow.request.flow.common.utils.PerformanceFuelEnergyCalculationCommonFunctionUtil;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputEnergyFuelDetails;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmissionsTradingSchemeDataCalculatedDataValidator {

    private static final String ERROR_MESSAGE = "%s %s: %s";

    public List<BusinessValidationResult> validateInputCalculatedData(final EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails,
                                                                      final MeasurementType measurementType) {
        if (ObjectUtils.isEmpty(energyFuelDetails)) {
            return List.of(BusinessValidationResult.valid());
        }

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate Primary energy
        String primaryErrorMessage = EmissionsTradingSchemeDataViolation.EmissionsTradingSchemeDataViolationMessage
                .INVALID_PRIMARY_ENERGY_DATA.getMessage();

        energyFuelDetails.getStandardFuels().entrySet().forEach(entry -> {
            BigDecimal expected = PerformanceFuelEnergyCalculationCommonFunctionUtil.PRIMARY_ENERGY_STANDARD_FUEL
                    .apply(entry, measurementType).setScale(7, RoundingMode.HALF_UP);

            validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(ERROR_MESSAGE, primaryErrorMessage, entry.getKey().name(), expected))
                    .process(expected, entry.getValue().getPrimaryEnergy()));
        });

        energyFuelDetails.getNonStandardFuels().forEach(fuel -> {
            BigDecimal expected = PerformanceFuelEnergyCalculationCommonFunctionUtil.PRIMARY_ENERGY_NON_STANDARD_FUEL
                    .apply(fuel, measurementType).setScale(7, RoundingMode.HALF_UP);

            validationResults.add(ValidatorHelper.validateBigDecimalEquals(String.format(ERROR_MESSAGE, primaryErrorMessage, fuel.getName(), expected))
                    .process(expected, fuel.getPrimaryEnergy()));
        });

        return validationResults;
    }

    public List<BusinessValidationResult> validateCalculatedResults(final EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails,
                                                                    final MeasurementType measurementType) {
        if (ObjectUtils.isEmpty(energyFuelDetails)) {
            return List.of(BusinessValidationResult.valid());
        }

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate Total Primary Energy
        BigDecimal expectedTotalPrimaryEnergy = PerformanceFuelEnergyCalculationCommonFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(energyFuelDetails, measurementType);

        String actualEnergyCarbonMessage = constructErrorMessage("totalPrimaryEnergy", expectedTotalPrimaryEnergy);
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(actualEnergyCarbonMessage)
                .process(expectedTotalPrimaryEnergy, energyFuelDetails.getCalculatedResults().getTotalPrimaryEnergy()));

        // Validate Total tCO2e emitted
        BigDecimal expectedTotalCo2Emissions = PerformanceFuelEnergyCalculationCommonFunctionUtil.ACTUAL_TCO2_EMISSIONS
                .apply(energyFuelDetails, measurementType);

        String actualCo2EmissionsMessage = constructErrorMessage("totalCo2Emissions", expectedTotalCo2Emissions);
        validationResults.add(ValidatorHelper.validateBigDecimalEquals(actualCo2EmissionsMessage)
                .process(expectedTotalCo2Emissions, energyFuelDetails.getCalculatedResults().getTotalCo2Emissions()));

        return validationResults;
    }

    private String constructErrorMessage(String field, Object expected) {
        String errorMessage = EmissionsTradingSchemeDataViolation.EmissionsTradingSchemeDataViolationMessage
                .INVALID_CALCULATED_RESULT_DATA.getMessage() + " %s: %s";

        return String.format(errorMessage, field, expected);
    }
}
