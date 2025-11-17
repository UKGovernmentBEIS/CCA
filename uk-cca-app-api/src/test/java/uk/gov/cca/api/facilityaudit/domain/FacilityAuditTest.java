package uk.gov.cca.api.facilityaudit.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType.*;

@ExtendWith(MockitoExtension.class)
class FacilityAuditTest {

	private Validator validator;

	@BeforeEach
	void setup() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Test
	void valid_required() {
		final FacilityAudit facilityAudit = FacilityAudit.builder()
				.facilityId(999L)
				.auditRequired(true)
				.reasons(List.of(ELIGIBILITY, SEVENTY_RULE_EVALUATION, BASE_YEAR_DATA, REPORTING_DATA, NON_COMPLIANCE, OTHER))
				.build();

		final Set<ConstraintViolation<FacilityAudit>> violations = validator.validate(facilityAudit);
		assertThat(violations).isEmpty();
	}

	@Test
	void invalid_empty_facilityId() {
		final FacilityAudit facilityAudit = FacilityAudit.builder()
				.build();

		final Set<ConstraintViolation<FacilityAudit>> violations = validator.validate(facilityAudit);
		assertThat(violations).hasSize(1);
		assertThat(violations).extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("must not be null");
		assertThat(violations).extracting(cv -> cv.getPropertyPath().toString())
				.containsExactlyInAnyOrder("facilityId");
	}
}