package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain;

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

class RequestedDocumentsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {

        final RequestedDocuments data = RequestedDocuments.builder()
                .annotatedSitePlansFile(UUID.randomUUID())
                .auditMaterialReceivedDate(LocalDate.of(2025, 9, 30))
                .build();

        final Set<ConstraintViolation<RequestedDocuments>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_not_valid() {
        final RequestedDocuments data = RequestedDocuments.builder()
                .auditMaterialReceivedDate(LocalDate.now().plusDays(1))
                .build();

        final Set<ConstraintViolation<RequestedDocuments>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must be a date in the past or in the present",
                        "{facilityAudit.preAuditReview.requestedDocuments}");
    }

    @Test
    void validate_received_audit_info_date_not_valid() {
        final RequestedDocuments data = RequestedDocuments.builder()
                .annotatedSitePlansFile(UUID.randomUUID())
                .auditMaterialReceivedDate(LocalDate.now().plusDays(1))
                .build();

        final Set<ConstraintViolation<RequestedDocuments>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must be a date in the past or in the present");
    }

    @Test
    void validate_at_least_one_document_not_valid() {
        final RequestedDocuments data = RequestedDocuments.builder()
                .auditMaterialReceivedDate(LocalDate.of(2024, 12, 12))
                .build();

        final Set<ConstraintViolation<RequestedDocuments>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{facilityAudit.preAuditReview.requestedDocuments}");
    }
}
