package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Apply70RuleTest {

	private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_all_fields_valid() {
    	Apply70Rule apply70Rule = Apply70Rule.builder()
    			.energyConsumed(BigDecimal.valueOf(65))
    			.energyConsumedProvision(BigDecimal.valueOf(12))
    			.energyConsumedEligible(BigDecimal.valueOf(72.8))
    			.startDate(LocalDate.now())
    			.evidenceFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<Apply70Rule>> violations = validator.validate(apply70Rule);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_energy_eligible_max_decimals_valid() {
    	Apply70Rule apply70Rule = Apply70Rule.builder()
    			.energyConsumed(BigDecimal.valueOf(65.52))
    			.energyConsumedProvision(BigDecimal.valueOf(40.33))
    			.energyConsumedEligible(BigDecimal.valueOf(91.944216))
    			.startDate(LocalDate.now())
    			.evidenceFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<Apply70Rule>> violations = validator.validate(apply70Rule);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_start_date_invalid() {
    	Apply70Rule apply70Rule = Apply70Rule.builder()
    			.energyConsumed(BigDecimal.valueOf(75))
    			.energyConsumedProvision(null)
    			.energyConsumedEligible(BigDecimal.valueOf(100))
    			.evidenceFile(UUID.randomUUID())
				.startDate(LocalDate.now())
    			.build();

        final Set<ConstraintViolation<Apply70Rule>> violations = validator.validate(apply70Rule);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.apply70rule.startDate}");
    }
	@Test
	void validate_no_start_date_valid() {
		Apply70Rule apply70Rule = Apply70Rule.builder()
				.energyConsumed(BigDecimal.valueOf(75))
				.energyConsumedProvision(null)
				.energyConsumedEligible(BigDecimal.valueOf(100))
				.evidenceFile(UUID.randomUUID())
				.startDate(null)
				.build();

		final Set<ConstraintViolation<Apply70Rule>> violations = validator.validate(apply70Rule);

		assertThat(violations).isEmpty();
	}
    @Test
    void validate_energy_eligible_not_100_invalid() {
    	Apply70Rule apply70Rule = Apply70Rule.builder()
    			.energyConsumed(BigDecimal.valueOf(90))
    			.energyConsumedEligible(BigDecimal.valueOf(70))
    			.evidenceFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<Apply70Rule>> violations = validator.validate(apply70Rule);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.apply70rule.energyConsumedEligible}");
    }
    
    @Test
    void validate_energy_eligible_calculation_not_correct_invalid() {
    	Apply70Rule apply70Rule = Apply70Rule.builder()
    			.energyConsumed(BigDecimal.valueOf(60))
    			.energyConsumedProvision(BigDecimal.valueOf(30))
    			.energyConsumedEligible(BigDecimal.valueOf(77))
    			.startDate(LocalDate.now())
    			.evidenceFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<Apply70Rule>> violations = validator.validate(apply70Rule);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.apply70rule.energyConsumedEligible}");
    }
    
    @Test
    void validate_energy_eligible_rounding_not_correct_invalid() {
    	Apply70Rule apply70Rule = Apply70Rule.builder()
    			.energyConsumed(BigDecimal.valueOf(65.52))
    			.energyConsumedProvision(BigDecimal.valueOf(40.33))
    			.energyConsumedEligible(BigDecimal.valueOf(91.95))
    			.startDate(LocalDate.now())
    			.evidenceFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<Apply70Rule>> violations = validator.validate(apply70Rule);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.apply70rule.energyConsumedEligible}");
    }
    
    @Test
    void validate_energy_consumed_null_invalid() {
    	Apply70Rule apply70Rule = Apply70Rule.builder()
    			.energyConsumed(null)
    			.energyConsumedProvision(BigDecimal.valueOf(40.33))
    			.energyConsumedEligible(BigDecimal.valueOf(91.95))
    			.startDate(null)
    			.evidenceFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<Apply70Rule>> violations = validator.validate(apply70Rule);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("{underlyingagreement.facilities.apply70rule.energyConsumedEligible}",
                		"{underlyingagreement.facilities.apply70rule.energyConsumedProvision}");
    }
    
    @Test
    void validate_energy_consumed_eligible_null_invalid() {
    	Apply70Rule apply70Rule = Apply70Rule.builder()
    			.energyConsumed(BigDecimal.valueOf(65.52))
    			.energyConsumedProvision(BigDecimal.valueOf(40.33))
    			.energyConsumedEligible(null)
    			.startDate(LocalDate.now())
    			.evidenceFile(UUID.randomUUID())
    			.build();

        final Set<ConstraintViolation<Apply70Rule>> violations = validator.validate(apply70Rule);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("{underlyingagreement.facilities.apply70rule.energyConsumedEligible}");
    }
    
    @Test
    void validate_all_null_invalid() {
    	Apply70Rule apply70Rule = Apply70Rule.builder()
    			.energyConsumed(null)
    			.energyConsumedProvision(null)
    			.energyConsumedEligible(null)
    			.startDate(null)
    			.evidenceFile(null)
    			.build();

        final Set<ConstraintViolation<Apply70Rule>> violations = validator.validate(apply70Rule);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsOnly("must not be null", "{underlyingagreement.facilities.apply70rule.energyConsumedEligible}");
    }
}
