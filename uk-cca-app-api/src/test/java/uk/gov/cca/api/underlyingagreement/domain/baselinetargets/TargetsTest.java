package uk.gov.cca.api.underlyingagreement.domain.baselinetargets;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TargetsTest {

	private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {
        final Targets data = Targets.builder()
                .improvement(BigDecimal.valueOf(50.345))
                .target(BigDecimal.valueOf(123.3451234))
                .build();
        final Set<ConstraintViolation<Targets>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_negative_improvement_valid() {
        final Targets data = Targets.builder()
                .improvement(BigDecimal.valueOf(-50.345))
                .target(BigDecimal.valueOf(123.3451234))
                .build();
        final Set<ConstraintViolation<Targets>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_negative_improvement_below_100_valid() {
        final Targets data = Targets.builder()
                .improvement(BigDecimal.valueOf(-101.345))
                .target(BigDecimal.valueOf(123.3451234))
                .build();
        final Set<ConstraintViolation<Targets>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }
}
