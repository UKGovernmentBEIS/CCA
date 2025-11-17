package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectiveActionFollowUpResponseTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {

        final CorrectiveActionFollowUpResponse data = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(true)
                .actionCarriedOutDate(LocalDate.now())
                .evidenceFiles(null)
                .comments("bla bla bla bla")
                .build();

        final Set<ConstraintViolation<CorrectiveActionFollowUpResponse>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_not_valid_when_has_carried_out_date() {

        final CorrectiveActionFollowUpResponse data = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(false)
                .actionCarriedOutDate(LocalDate.now())
                .comments("bla bla bla bla")
                .build();

        final Set<ConstraintViolation<CorrectiveActionFollowUpResponse>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{facilityAudit.trackcorrectiveactions.correctiveActionFollowUpResponse}");
    }

    @Test
    void validate_valid_without_action_carried_out() {

        final CorrectiveActionFollowUpResponse data = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(false)
                .comments("bla bla bla bla")
                .build();

        final Set<ConstraintViolation<CorrectiveActionFollowUpResponse>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_not_mutually_valid() {

        final CorrectiveActionFollowUpResponse data = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(false)
                .actionCarriedOutDate(LocalDate.now().plusDays(1L))
                .evidenceFiles(null)
                .comments("bla bla bla bla")
                .build();

        final Set<ConstraintViolation<CorrectiveActionFollowUpResponse>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{facilityAudit.trackcorrectiveactions.correctiveActionFollowUpResponse}",
                        "must be a date in the past or in the present");
    }

    @Test
    void validate_not_valid_null_values() {

        final CorrectiveActionFollowUpResponse data = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(null)
                .actionCarriedOutDate(LocalDate.now().plusDays(1L))
                .evidenceFiles(null)
                .comments("bla bla bla bla")
                .build();

        final Set<ConstraintViolation<CorrectiveActionFollowUpResponse>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must not be null", "{facilityAudit.trackcorrectiveactions.correctiveActionFollowUpResponse}",
                        "must be a date in the past or in the present");
    }

    @Test
    void validate_not_valid_missing_comments() {

        final CorrectiveActionFollowUpResponse data = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(true)
                .actionCarriedOutDate(LocalDate.now())
                .evidenceFiles(null)
                .comments(null)
                .build();

        final Set<ConstraintViolation<CorrectiveActionFollowUpResponse>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{facilityAudit.trackcorrectiveactions.correctiveActionFollowUpResponse}");
    }
}
