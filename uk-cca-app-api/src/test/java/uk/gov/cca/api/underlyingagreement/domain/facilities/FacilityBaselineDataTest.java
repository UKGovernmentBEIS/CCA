package uk.gov.cca.api.underlyingagreement.domain.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class FacilityBaselineDataTest {

	private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_facility_baseline_data_valid() {
        final FacilityBaselineData data = FacilityBaselineData.builder()
                .isTwelveMonths(Boolean.TRUE)
                .baselineDate(LocalDate.of(2022, 1, 1))
                .energy(BigDecimal.valueOf(10.999))
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<FacilityBaselineData>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_facility_baseline_data_baseline_date_invalid() {
        final FacilityBaselineData data = FacilityBaselineData.builder()
                .isTwelveMonths(Boolean.TRUE)
                .baselineDate(LocalDate.of(2021, 1, 1))
                .explanation("My explanation")
                .energy(BigDecimal.TEN)
                .energyCarbonFactor(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<FacilityBaselineData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.baselineData.baselineDate}");
    }
}
