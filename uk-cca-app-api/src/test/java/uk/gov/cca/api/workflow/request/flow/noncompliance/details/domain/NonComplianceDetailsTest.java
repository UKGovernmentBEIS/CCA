package uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NonComplianceDetailsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {

        final NonComplianceDetails data = NonComplianceDetails.builder()
                .nonComplianceType(NonComplianceType.FAILURE_TO_NOTIFY_OF_AN_ERROR)
                .isEnforcementResponseNoticeRequired(true)
                .compliantDate(LocalDate.now())
                .build();

        final Set<ConstraintViolation<NonComplianceDetails>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_not_valid() {

        final NonComplianceDetails data = NonComplianceDetails.builder()
                .isEnforcementResponseNoticeRequired(false)
                .compliantDate(LocalDate.now().plusDays(1))
                .build();

        final Set<ConstraintViolation<NonComplianceDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must not be null",
                        "{nonCompliance.nonComplianceDetails.explanation}",
                        "must be a date in the past or in the present");
    }
}
