package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuditReasonDetailsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {

        final AuditReasonDetails data = AuditReasonDetails.builder()
                .reasonsForAudit(List.of(FacilityAuditReasonType.REPORTING_DATA))
                .comment("bla bla bla bla bla bla bla bla bla bla bla")
                .build();

        final Set<ConstraintViolation<AuditReasonDetails>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_not_valid() {

        final AuditReasonDetails data = AuditReasonDetails.builder()
                .reasonsForAudit(List.of())
                .comment(null)
                .build();

        final Set<ConstraintViolation<AuditReasonDetails>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must not be null", "must not be empty");
    }
}
