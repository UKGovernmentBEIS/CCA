package uk.gov.cca.api.underlyingagreement.domain.baselinetargets;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BaselineDataTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_twelve_months_valid() {
        final BaselineData data = BaselineData.builder()
                .isTwelveMonths(Boolean.TRUE)
                .baselineDate(LocalDate.now())
                .explanation("My explanation")
                .energy(BigDecimal.valueOf(10.999))
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<BaselineData>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }
    
    @Test
    void validate_facility_over_3_decimals_not_valid() {
        final BaselineData data = BaselineData.builder()
                .isTwelveMonths(Boolean.TRUE)
                .baselineDate(LocalDate.now())
                .explanation("My explanation")
                .energy(BigDecimal.valueOf(10.9999))
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<BaselineData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void validate_twelve_months_no_explanation_not_valid() {
        final BaselineData data = BaselineData.builder()
                .isTwelveMonths(Boolean.TRUE)
                .baselineDate(LocalDate.now())
                .energy(BigDecimal.TEN)
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<BaselineData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.baselineData.explanation}");
    }

    @Test
    void validate_twelve_months_2018_no_explanation_valid() {
        final BaselineData data = BaselineData.builder()
                .isTwelveMonths(Boolean.TRUE)
                .baselineDate(LocalDate.of(2018, 1, 1))
                .energy(BigDecimal.TEN)
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<BaselineData>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_twelve_months_2018_with_explanation_not_valid() {
        final BaselineData data = BaselineData.builder()
                .isTwelveMonths(Boolean.TRUE)
                .baselineDate(LocalDate.of(2018, 1, 1))
                .explanation("My explanation")
                .energy(BigDecimal.TEN)
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<BaselineData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.baselineData.explanation}");
    }

    @Test
    void validate_twelve_months_with_greenfieldEvidences_not_valid() {
        final BaselineData data = BaselineData.builder()
                .isTwelveMonths(Boolean.TRUE)
                .baselineDate(LocalDate.now())
                .explanation("My explanation")
                .greenfieldEvidences(Set.of(UUID.randomUUID()))
                .energy(BigDecimal.TEN)
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<BaselineData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.baselineData.greenfieldEvidences}");
    }

    @Test
    void validate_no_twelve_months_valid() {
        final BaselineData data = BaselineData.builder()
                .isTwelveMonths(Boolean.FALSE)
                .baselineDate(LocalDate.now())
                .explanation("My explanation")
                .energy(BigDecimal.TEN)
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<BaselineData>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_no_twelve_months_no_explanation_not_valid() {
        final BaselineData data = BaselineData.builder()
                .isTwelveMonths(Boolean.FALSE)
                .baselineDate(LocalDate.now())
                .energy(BigDecimal.TEN)
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<BaselineData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.baselineData.explanation}");
    }

    @Test
    void validate_no_twelve_months_with_greenfieldEvidences_valid() {
        final BaselineData data = BaselineData.builder()
                .isTwelveMonths(Boolean.FALSE)
                .baselineDate(LocalDate.now())
                .explanation("My explanation")
                .greenfieldEvidences(Set.of(UUID.randomUUID()))
                .energy(BigDecimal.TEN)
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<BaselineData>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_no_data_not_valid() {
        final BaselineData data = BaselineData.builder().build();
        final Set<ConstraintViolation<BaselineData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty().hasSize(5);
    }
}
