package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFuel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL;
import static uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS;

class EmissionsTradingSchemeDataContainerTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid_small_emitter() {

        EmissionsTradingSchemeDataContainer data = EmissionsTradingSchemeDataContainer.builder()
                .isUltraSmallEmitter(true)
                .measurementType(MeasurementType.ENERGY_KWH)
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataContainer>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_valid_small_emitter_with_fuel_details() {

        EmissionsTradingSchemeDataContainer data = EmissionsTradingSchemeDataContainer.builder()
                .isUltraSmallEmitter(true)
                .measurementType(MeasurementType.ENERGY_KWH)
                .fuels(
                        List.of(PerformanceDataFacilityFuel.builder()
                                        .name(NATURAL_GAS.getDescription())
                                        .fixedConversionFactorCode(NATURAL_GAS)
                                        .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(NATURAL_GAS, MeasurementType.ENERGY_KWH))
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryConversionFactor(NATURAL_GAS.getPrimaryFactor())
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build(),
                                PerformanceDataFacilityFuel.builder()
                                        .name(GAS_DIESEL_OIL.getDescription())
                                        .fixedConversionFactorCode(GAS_DIESEL_OIL)
                                        .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(GAS_DIESEL_OIL, MeasurementType.ENERGY_KWH))
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryConversionFactor(GAS_DIESEL_OIL.getPrimaryFactor())
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build(),
                                PerformanceDataFacilityFuel.builder()
                                        .name("Biofuel")
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .deliveredEnergy(BigDecimal.valueOf(1500000))
                                        .primaryConversionFactor(BigDecimal.ONE)
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .build()
                        ))
                .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                        .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                        .totalCo2Emissions(BigDecimal.valueOf(2000000))
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataContainer>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_with_fuel_details_invalid() {

        EmissionsTradingSchemeDataContainer data = EmissionsTradingSchemeDataContainer.builder()
                .isUltraSmallEmitter(false)
                .measurementType(MeasurementType.ENERGY_KWH)
                .fuels(List.of())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataContainer>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{emissionstradingschemedata.energyFuelDetails.notEmpty}");
    }

    @Test
    void validate_with_no_fuels_and_calculated_results_invalid() {

        EmissionsTradingSchemeDataContainer data = EmissionsTradingSchemeDataContainer.builder()
                .isUltraSmallEmitter(true)
                .measurementType(MeasurementType.ENERGY_KWH)
                .fuels(List.of())
                .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                        .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                        .totalCo2Emissions(BigDecimal.valueOf(2000000))
                        .build())
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataContainer>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{emissionstradingschemedata.calculatedResults}");
    }

    @Test
    void validate_with_fuels_and_no_calculated_results_invalid() {

        EmissionsTradingSchemeDataContainer data = EmissionsTradingSchemeDataContainer.builder()
                .isUltraSmallEmitter(false)
                .measurementType(MeasurementType.ENERGY_KWH)
                .fuels(List.of(PerformanceDataFacilityFuel.builder()
                        .name(NATURAL_GAS.getDescription())
                        .fixedConversionFactorCode(NATURAL_GAS)
                        .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(NATURAL_GAS, MeasurementType.ENERGY_KWH))
                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                        .primaryConversionFactor(NATURAL_GAS.getPrimaryFactor())
                        .primaryEnergy(BigDecimal.valueOf(5000000))
                        .build()))
                .build();

        final Set<ConstraintViolation<EmissionsTradingSchemeDataContainer>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{emissionstradingschemedata.calculatedResults}");
    }
}
