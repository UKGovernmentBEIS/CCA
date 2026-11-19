package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityNonStandardFuel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EmissionsTradingSchemeDataInputDetailsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid_small_emitter() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(false)
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_valid_small_emitter_with_fuel_details() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(true)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
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
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(2000000))
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_valid_data_with_fuel_details() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(false)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
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
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(2000000))
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_small_emitter_with_provide_fuel_details_not_valid() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(true)
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{emissionstradingschemedata.energyFuelDetails.invalid}");
    }

    @Test
    void validate_input_data_not_valid() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .provideFuelDetails(true)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
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
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(2000000))
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }


    @Test
    void validate_small_emitter_with_fuel_details_not_valid() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(false)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .build()
                        ))
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(2000000))
                                .totalCo2Emissions(BigDecimal.valueOf(2000000))
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{emissionstradingschemedata.energyFuelDetails.invalid}");
    }

    @Test
    void validate_data_with_only_fuel_details() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .build()
                        ))
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(2000000))
                                .totalCo2Emissions(BigDecimal.valueOf(2000000))
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void validate_empty_input_details() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder().build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void validate_with_not_valid_fuel_details() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(false)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
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
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must not be null", "must not be null");
    }

    @Test
    void validate_without_fuel_details_not_valid_() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(false)
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{emissionstradingschemedata.energyFuelDetails.invalid}");
    }

    @Test
    void validate_with_no_fuel_details_not_valid_() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .provideFuelDetails(true)
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void validate_zero_value_delivery_energy_invalid() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(true)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.ZERO)
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
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(2000000))
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must be greater than 0");
    }

    @Test
    void validate_empty_fuels_not_valid() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(true)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of())
                        .nonStandardFuels(List.of())
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(2000000))
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{emissionstradingschemedata.energyFuelDetails.notEmpty}");
    }

    @Test
    void validate_with_standard_fuel_type_not_valid() {

        EmissionsTradingSchemeDataInputDetails data = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(false)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                // not a valid fuel type for ETS
                                PerformanceDataFacilityFixedConversionFactor.NITROGEN_COOLING, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(2000000))
                                        .primaryEnergy(BigDecimal.valueOf(2000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(500000))
                                        .primaryEnergy(BigDecimal.valueOf(2000000))
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
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(5000000))
                                .build())
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataInputDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{emissionstradingschemedata.standardFuels.invalid}");
    }
}
