package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataCalculatedResults;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputEnergyFuelDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataCalculatedDataValidatorTest {

    @InjectMocks
    private EmissionsTradingSchemeDataCalculatedDataValidator calculatedDataValidator;

    @Test
    void validateInputCalculatedData_valid() {
        EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails = EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                .standardFuels(Map.of(
                        PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(2000000))
                                .primaryEnergy(BigDecimal.valueOf(2000000))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(500000))
                                .primaryEnergy(BigDecimal.valueOf(500000))
                                .build()
                ))
                .nonStandardFuels(List.of(
                        PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000))
                                .conversionFactor(BigDecimal.valueOf(0.10046))
                                .build()
                ))
                .build();

        // invoke
        List<BusinessValidationResult> businessValidationResults =
                calculatedDataValidator.validateInputCalculatedData(energyFuelDetails, MeasurementType.ENERGY_KWH);

        // verify
        boolean hasInvalidResult = businessValidationResults.stream().anyMatch(result -> !result.isValid());
        assertFalse(hasInvalidResult);
    }

    @Test
    void validateInputCalculatedData_valid_no_fuels() {
        // invoke
        List<BusinessValidationResult> businessValidationResults =
                calculatedDataValidator.validateInputCalculatedData(null, MeasurementType.ENERGY_KWH);

        // verify
        boolean hasInvalidResult = businessValidationResults.stream().anyMatch(result -> !result.isValid());
        assertFalse(hasInvalidResult);
    }

    @Test
    void validateInputCalculatedData_carbon_valid() {
        EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails = EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                .standardFuels(Map.of(
                        PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryEnergy(BigDecimal.valueOf(912700))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(2000000))
                                .primaryEnergy(BigDecimal.valueOf(428980))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryEnergy(BigDecimal.valueOf(1283950))
                                .build()
                ))
                .nonStandardFuels(List.of(
                        PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(150690))
                                .conversionFactor(BigDecimal.valueOf(0.10046))
                                .build()
                ))
                .build();

        // invoke
        List<BusinessValidationResult> businessValidationResults =
                calculatedDataValidator.validateInputCalculatedData(energyFuelDetails, MeasurementType.CARBON_KG);

        // verify
        boolean hasInvalidResult = businessValidationResults.stream().anyMatch(result -> !result.isValid());
        assertFalse(hasInvalidResult);
    }

    @Test
    void validateInputCalculatedData_carbon_tonne_valid() {
        EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails = EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                .standardFuels(Map.of(
                        PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryEnergy(BigDecimal.valueOf(912.700))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(2000000))
                                .primaryEnergy(BigDecimal.valueOf(428.980))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryEnergy(BigDecimal.valueOf(1283.950))
                                .build()
                ))
                .nonStandardFuels(List.of(
                        PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(150.690))
                                .conversionFactor(BigDecimal.valueOf(0.10046))
                                .build()
                ))
                .build();

        // invoke
        List<BusinessValidationResult> businessValidationResults =
                calculatedDataValidator.validateInputCalculatedData(energyFuelDetails, MeasurementType.CARBON_TONNE);

        // verify
        boolean hasInvalidResult = businessValidationResults.stream().anyMatch(result -> !result.isValid());
        assertFalse(hasInvalidResult);
    }

    @Test
    void validateInputCalculatedData_invalid() {
        EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails = EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                .standardFuels(Map.of(
                        PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(2000000))
                                .primaryEnergy(BigDecimal.valueOf(2000000))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(500000))
                                .primaryEnergy(BigDecimal.valueOf(600000))
                                .build()
                ))
                .nonStandardFuels(List.of(
                        PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000))
                                .conversionFactor(BigDecimal.valueOf(0.10046))
                                .build()
                ))
                .build();

        // invoke
        List<BusinessValidationResult> businessValidationResults =
                calculatedDataValidator.validateInputCalculatedData(energyFuelDetails, MeasurementType.ENERGY_KWH);

        // verify
        boolean hasInvalidResult = businessValidationResults.stream().anyMatch(result -> !result.isValid());
        assertTrue(hasInvalidResult);
    }

    @Test
    void validateCalculatedResults_valid() {
        EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails = EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                .standardFuels(Map.of(
                        PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(2000000))
                                .primaryEnergy(BigDecimal.valueOf(2000000))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build()
                ))
                .nonStandardFuels(List.of(
                        PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000))
                                .conversionFactor(BigDecimal.valueOf(0.10046))
                                .build()
                ))
                .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                        .totalPrimaryEnergy(BigDecimal.valueOf(13500000))
                        .totalCo2Emissions(BigDecimal.valueOf(2776.3200000))
                        .build())
                .build();

        // invoke
        List<BusinessValidationResult> businessValidationResults = calculatedDataValidator.validateCalculatedResults(energyFuelDetails, MeasurementType.ENERGY_KWH);

        // verify
        boolean hasInvalidResult = businessValidationResults.stream().anyMatch(result -> !result.isValid());
        assertFalse(hasInvalidResult);
    }

    @Test
    void validateCalculatedResults_invalid() {
        EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails = EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                .standardFuels(Map.of(
                        PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(2000000))
                                .primaryEnergy(BigDecimal.valueOf(2000000))
                                .build(),
                        PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build()
                ))
                .nonStandardFuels(List.of(
                        PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000))
                                .conversionFactor(BigDecimal.valueOf(0.10046))
                                .build()
                ))
                .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                        .totalPrimaryEnergy(BigDecimal.valueOf(12500000)) // wrong result
                        .totalCo2Emissions(BigDecimal.valueOf(2641.329)) // wrong result
                        .build())
                .build();

        // invoke
        List<BusinessValidationResult> businessValidationResults = calculatedDataValidator.validateCalculatedResults(energyFuelDetails, MeasurementType.ENERGY_KWH);

        // verify
        boolean hasInvalidResult = businessValidationResults.stream().allMatch(result -> !result.isValid());
        assertTrue(hasInvalidResult);
    }
}
