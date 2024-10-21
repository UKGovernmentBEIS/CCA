package uk.gov.cca.api.sectorassociation.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TargetSetDTOTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAbsoluteAndThroughputUnitIsNotNull_thenValid() {
        TargetSetDTO targetSet = TargetSetDTO.builder()
                .id(1L)
                .targetCurrencyType("Absolute")
                .energyOrCarbonUnit("kWh")
                .throughputUnit("Throughput unit")
                .build();

        Set<ConstraintViolation<TargetSetDTO>> violations = validator.validate(targetSet);
        assertTrue(violations.isEmpty(), "Expected no validation errors." );
    }

    @Test
    void whenAbsoluteAndThroughputUnitIsNull_thenInvalid() {
        TargetSetDTO targetSet = TargetSetDTO.builder()
                .id(1L)
                .targetCurrencyType("Absolute")
                .throughputUnit(null)
                .energyOrCarbonUnit("kWh")
                .build();

        Set<ConstraintViolation<TargetSetDTO>> violations = validator.validate(targetSet);
        assertFalse(violations.isEmpty(), "Expected validation errors");
    }
    @Test
    void whenRelativeAndThroughputUnitIsNull_thenInvalid() {
        TargetSetDTO targetSet = TargetSetDTO.builder()
                .id(1L)
                .targetCurrencyType("Relative")
                .throughputUnit(null)
                .energyOrCarbonUnit("kWh")
                .build();

        Set<ConstraintViolation<TargetSetDTO>> violations = validator.validate(targetSet);
        assertFalse(violations.isEmpty(), "Expected validation errors");
    }

    @Test
    void whenRelativeAndThroughputUnitIsNotNull_thenValid() {
        TargetSetDTO targetSet = TargetSetDTO.builder()
                .id(1L)
                .targetCurrencyType("Relative")
                .energyOrCarbonUnit("kWh")
                .throughputUnit("Throughput unit")
                .build();

        Set<ConstraintViolation<TargetSetDTO>> violations = validator.validate(targetSet);
        assertTrue(violations.isEmpty(), "Expected no validation errors." );
    }

    @Test
    void whenNotAbsoluteOrRelativeAndThroughputUnitIsNull_thenValid() {
        TargetSetDTO targetSet = TargetSetDTO.builder()
                .id(1L)
                .targetCurrencyType("OTHER")
                .throughputUnit(null)
                .energyOrCarbonUnit("someEnergyUnit")
                .build();

        Set<ConstraintViolation<TargetSetDTO>> violations = validator.validate(targetSet);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void whenThroughputUnitLongerThanTheValidation_thenInvalid(){
        TargetSetDTO targetSet = TargetSetDTO.builder()
                .id(1L)
                .targetCurrencyType("E".repeat(256))
                .throughputUnit(null)
                .energyOrCarbonUnit("someEnergyUnit")
                .build();

        Set<ConstraintViolation<TargetSetDTO>> violations = validator.validate(targetSet);
        assertFalse(violations.isEmpty(), "Expected no validation errors");
    }
}
