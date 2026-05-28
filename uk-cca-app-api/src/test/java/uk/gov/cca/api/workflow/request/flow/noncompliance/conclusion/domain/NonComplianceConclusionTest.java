package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NonComplianceConclusionTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {

        final NonComplianceConclusion data = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.NONE)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(null)
                .build();

        final Set<ConstraintViolation<NonComplianceConclusion>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_withdraw_not_valid() {

        final NonComplianceConclusion data = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.NONE)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(NonComplianceWithdrawNotice.builder()
                        .file(null)
                        .comments("bla bla bla bla")
                        .build())
                .build();

        final Set<ConstraintViolation<NonComplianceConclusion>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{nonCompliance.nonComplianceConclusion.withdrawNotice}",
                        "must not be null");
    }

    @Test
    void validate_not_valid() {

        final NonComplianceConclusion data = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.NONE)
                        .penaltyPaid(true)
                        .penaltyPaymentDate(LocalDate.now().plusDays(1))
                        .build())
                .withdrawNotice(null)
                .build();

        final Set<ConstraintViolation<NonComplianceConclusion>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must not be null",
                        "{nonCompliance.nonComplianceConclusion.complianceRestoredDate}",
                        "must be a date in the past or in the present");
    }

    @Test
    void validate_withdraw_not_empty() {

        final NonComplianceConclusion data = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.WITHDRAW)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(null)
                .build();

        final Set<ConstraintViolation<NonComplianceConclusion>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{nonCompliance.nonComplianceConclusion.withdrawNotice}");
    }

    @Test
    void validate_withdraw_must_be_empty() {

        final NonComplianceConclusion data = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.REISSUE)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(NonComplianceWithdrawNotice.builder()
                        .file(UUID.randomUUID())
                        .comments("bla bla bla bla")
                        .build())
                .build();

        final Set<ConstraintViolation<NonComplianceConclusion>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{nonCompliance.nonComplianceConclusion.withdrawNotice}");
    }
}
