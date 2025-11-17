package uk.gov.cca.api.facilityaudit.domain.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FacilityAuditUpdateDTOTest {

	private static Validator validator;

	@BeforeAll
	static void setUpValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void valid_FacilityAuditUpdateDTO_required() {
		final FacilityAuditUpdateDTO updateDTO = FacilityAuditUpdateDTO.builder()
				.auditRequired(true)
				.comments("Comments")
				.reasons(new ArrayList<>(List.of(FacilityAuditReasonType.ELIGIBILITY, FacilityAuditReasonType.SEVENTY_RULE_EVALUATION)))
				.build();

		Set<ConstraintViolation<FacilityAuditUpdateDTO>> violations = validator.validate(updateDTO);

		assertThat(violations).isEmpty();
	}

	@Test
	void valid_FacilityAuditUpdateDTO_not_required() {
		final FacilityAuditUpdateDTO updateDTO = FacilityAuditUpdateDTO.builder()
				.auditRequired(false)
				.build();

		Set<ConstraintViolation<FacilityAuditUpdateDTO>> violations = validator.validate(updateDTO);

		assertThat(violations).isEmpty();
	}

	@Test
	void invalid_FacilityAuditUpdateDTO_required() {
		final FacilityAuditUpdateDTO updateDTO = FacilityAuditUpdateDTO.builder()
				.auditRequired(true)
				.reasons(List.of())
				.build();

		Set<ConstraintViolation<FacilityAuditUpdateDTO>> violations = validator.validate(updateDTO);

		assertThat(violations).isNotEmpty().hasSize(2);
		assertThat(violations).extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("{facilityAudit.edit.comments}", "{facilityAudit.edit.reasons}");
	}

	@Test
	void invalid_FacilityAuditUpdateDTO_not_required() {
		final FacilityAuditUpdateDTO updateDTO = FacilityAuditUpdateDTO.builder()
				.auditRequired(false)
				.comments("Comments")
				.reasons(new ArrayList<>(List.of(FacilityAuditReasonType.ELIGIBILITY, FacilityAuditReasonType.SEVENTY_RULE_EVALUATION)))
				.build();

		Set<ConstraintViolation<FacilityAuditUpdateDTO>> violations = validator.validate(updateDTO);

		assertThat(violations).isNotEmpty().hasSize(2);
		assertThat(violations).extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("{facilityAudit.edit.comments}", "{facilityAudit.edit.reasons}");
	}

	@Test
	void invalid_FacilityAuditUpdateDTO_auditRequired_missing() {
		final FacilityAuditUpdateDTO updateDTO = FacilityAuditUpdateDTO.builder()
				.comments("S".repeat(10001))
				.reasons(new ArrayList<>(List.of(FacilityAuditReasonType.ELIGIBILITY, FacilityAuditReasonType.SEVENTY_RULE_EVALUATION)))
				.build();

		Set<ConstraintViolation<FacilityAuditUpdateDTO>> violations = validator.validate(updateDTO);

		assertThat(violations).isNotEmpty().hasSize(4);
		assertThat(violations).extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder("must not be null", "size must be between 0 and 10000", "{facilityAudit.edit.comments}",
						"{facilityAudit.edit.reasons}");
	}
}