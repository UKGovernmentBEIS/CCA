package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectiveActionsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {
        final CorrectiveAction correctiveAction = CorrectiveAction.builder()
                .title("title")
                .details("details")
                .deadline(LocalDate.of(2022, 2, 2))
                .build();

        CorrectiveActions data = CorrectiveActions.builder()
                .hasActions(true)
                .actions(Set.of(correctiveAction))
                .build();

        Set<ConstraintViolation<CorrectiveActions>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_not_valid() {
        CorrectiveActions data = CorrectiveActions.builder()
                .hasActions(true)
                .build();

        Set<ConstraintViolation<CorrectiveActions>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{facilityAudit.auditdetailscorrectiveactions.hasActions}");
    }

    @Test
    void validate_not_valid_exceeds_max_limit() {
        final Set<CorrectiveAction> elevenCorrectiveActions = getElevenCorrectiveActions();

        CorrectiveActions data = CorrectiveActions.builder()
                .hasActions(true)
                .actions(elevenCorrectiveActions)
                .build();

        Set<ConstraintViolation<CorrectiveActions>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("size must be between 0 and 10");
    }

    @Test
    void validate_not_mutually_valid() {
        final CorrectiveAction correctiveAction = CorrectiveAction.builder()
                .title(null)
                .details("details")
                .deadline(LocalDate.of(2022, 2, 2))
                .build();

        CorrectiveActions data = CorrectiveActions.builder()
                .hasActions(false)
                .actions(Set.of(correctiveAction))
                .build();

        Set<ConstraintViolation<CorrectiveActions>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must not be null", "{facilityAudit.auditdetailscorrectiveactions.hasActions}");
    }

    @Test
    void validate_not_mutually_valid_and_max_limit_exceeds() {
        final Set<CorrectiveAction> elevenCorrectiveActions = getElevenCorrectiveActions();

        CorrectiveActions data = CorrectiveActions.builder()
                .hasActions(false)
                .actions(elevenCorrectiveActions)
                .build();

        Set<ConstraintViolation<CorrectiveActions>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{facilityAudit.auditdetailscorrectiveactions.hasActions}",
                        "size must be between 0 and 10");
    }

    private Set<CorrectiveAction> getElevenCorrectiveActions() {
        final Set<CorrectiveAction> correctiveActions = new LinkedHashSet<>();
        for (int i = 0; i <= 10; i++) {
            correctiveActions.add(CorrectiveAction.builder()
                    .title("Corrective Action " + i)
                    .details("details")
                    .deadline(LocalDate.of(2022, 2, 2))
                    .build());
        }
        return correctiveActions;
    }
}
