package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuditDeterminationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {

        final AuditDetermination data = AuditDetermination.builder()
                .reviewCompletionDate(LocalDate.of(2024, 12, 12))
                .furtherAuditNeeded(true)
                .reviewComments("bla bla bla bla bla bla bla bla bla bla bla")
                .build();

        final Set<ConstraintViolation<AuditDetermination>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_not_valid() {

        final AuditDetermination data = AuditDetermination.builder()
                .reviewCompletionDate(LocalDate.now().plusDays(1))
                .furtherAuditNeeded(null)
                .reviewComments("bla bla bla bla bla bla bla bla bla bla bla")
                .build();

        final Set<ConstraintViolation<AuditDetermination>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must be a date in the past or in the present",
                        "must not be null");
    }
}
