package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class VariationDeterminationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void variationDetermination_reject_valid() {
        final VariationDetermination determination = VariationDetermination.builder()
                .determination(Determination.builder()
                        .type(DeterminationType.REJECTED)
                        .reason("reason")
                        .build())
                .build();

        Set<ConstraintViolation<VariationDetermination>> violations = validator.validate(determination);

        assertThat(violations).isEmpty();
    }

    @Test
    void variationDetermination_reject_with_changes_not_valid() {
        final VariationDetermination determination = VariationDetermination.builder()
                .variationImpactsAgreement(true)
                .determination(Determination.builder()
                        .type(DeterminationType.REJECTED)
                        .reason("reason")
                        .build())
                .build();

        Set<ConstraintViolation<VariationDetermination>> violations = validator.validate(determination);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.variation.review.variationDetermination.variationImpactsAgreement.typeMismatch}");
    }

    @Test
    void variationDetermination_reject_with_no_changes_not_valid() {
        final VariationDetermination determination = VariationDetermination.builder()
                .variationImpactsAgreement(false)
                .determination(Determination.builder()
                        .type(DeterminationType.REJECTED)
                        .reason("reason")
                        .build())
                .build();

        Set<ConstraintViolation<VariationDetermination>> violations = validator.validate(determination);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "{underlyingagreement.variation.review.variationDetermination.variationImpactsAgreement.typeMismatch}",
                        "{underlyingagreement.variation.review.variationDetermination.additionalInformation.typeMismatch}");
    }

    @Test
    void variationDetermination_accept_with_changes_valid() {
        final VariationDetermination determination = VariationDetermination.builder()
                .variationImpactsAgreement(true)
                .determination(Determination.builder()
                        .type(DeterminationType.ACCEPTED)
                        .additionalInformation("additional information")
                        .build())
                .build();

        Set<ConstraintViolation<VariationDetermination>> violations = validator.validate(determination);

        assertThat(violations).isEmpty();
    }

    @Test
    void variationDetermination_accept_with_reason_not_valid() {
        final VariationDetermination determination = VariationDetermination.builder()
                .variationImpactsAgreement(true)
                .determination(Determination.builder()
                        .type(DeterminationType.ACCEPTED)
                        .reason("reason")
                        .additionalInformation("additional information")
                        .build())
                .build();

        Set<ConstraintViolation<VariationDetermination>> violations = validator.validate(determination);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.review.determination.reason.typeMismatch}");
    }

    @Test
    void variationDetermination_accept_with_changes_no_additional_info_valid() {
        final VariationDetermination determination = VariationDetermination.builder()
                .variationImpactsAgreement(true)
                .determination(Determination.builder()
                        .type(DeterminationType.ACCEPTED)
                        .build())
                .build();

        Set<ConstraintViolation<VariationDetermination>> violations = validator.validate(determination);

        assertThat(violations).isEmpty();
    }

    @Test
    void variationDetermination_accept_with_no_changes_valid() {
        final VariationDetermination determination = VariationDetermination.builder()
                .variationImpactsAgreement(false)
                .determination(Determination.builder()
                        .type(DeterminationType.ACCEPTED)
                        .additionalInformation("additional information")
                        .build())
                .build();

        Set<ConstraintViolation<VariationDetermination>> violations = validator.validate(determination);

        assertThat(violations).isEmpty();
    }

    @Test
    void variationDetermination_accept_with_no_changes_not_valid() {
        final VariationDetermination determination = VariationDetermination.builder()
                .variationImpactsAgreement(false)
                .determination(Determination.builder()
                        .type(DeterminationType.ACCEPTED)
                        .build())
                .build();

        Set<ConstraintViolation<VariationDetermination>> violations = validator.validate(determination);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.variation.review.variationDetermination.additionalInformation.typeMismatch}");
    }

    @Test
    void variationDetermination_no_determination_not_valid() {
        final VariationDetermination determination = VariationDetermination.builder()
                .build();

        Set<ConstraintViolation<VariationDetermination>> violations = validator.validate(determination);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void variationDetermination_empty_determination_not_valid() {
        final VariationDetermination determination = VariationDetermination.builder()
                .determination(Determination.builder().build())
                .build();

        Set<ConstraintViolation<VariationDetermination>> violations = validator.validate(determination);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }
}
