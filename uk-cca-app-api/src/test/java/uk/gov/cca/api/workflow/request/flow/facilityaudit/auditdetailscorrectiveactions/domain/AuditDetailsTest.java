package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain;

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

class AuditDetailsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {

        AuditDetails data = AuditDetails.builder()
                .auditTechnique(AuditTechnique.DESK_BASED_INTERVIEW)
                .auditDate(LocalDate.now())
                .comments("bla bla bla bla")
                .finalAuditReportDate(LocalDate.of(2022, 2, 2))
                .auditDocuments(Set.of(UUID.randomUUID()))
                .build();

        final Set<ConstraintViolation<AuditDetails>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_not_valid() {

        AuditDetails data = AuditDetails.builder()
                .auditTechnique(AuditTechnique.DESK_BASED_INTERVIEW)
                .auditDate(LocalDate.now().plusDays(1))
                .finalAuditReportDate(LocalDate.of(2022, 2, 2))
                .build();

        final Set<ConstraintViolation<AuditDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must not be empty", "must not be null",
                        "must be a date in the past or in the present");
    }
}
