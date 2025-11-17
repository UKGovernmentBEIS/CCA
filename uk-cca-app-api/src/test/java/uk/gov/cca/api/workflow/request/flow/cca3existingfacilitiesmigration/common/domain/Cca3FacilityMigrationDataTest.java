package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class Cca3FacilityMigrationDataTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {
        final Cca3FacilityMigrationData data = Cca3FacilityMigrationData.builder()
                .accountBusinessId("account")
                .facilityBusinessId("facility")
                .facilityName("name")
                .participatingInCca3Scheme(Boolean.TRUE)
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .energyCarbonFactor(BigDecimal.valueOf(10.999))
                .usedReportingMechanism(Boolean.TRUE)
                .tp7Improvement(BigDecimal.TEN)
                .tp8Improvement(BigDecimal.TEN)
                .tp9Improvement(BigDecimal.TEN)
                .totalFixedEnergy(BigDecimal.valueOf(100.999))
                .totalVariableEnergy(BigDecimal.ZERO)
                .totalThroughput(BigDecimal.valueOf(100.999))
                .throughputUnit("unit")
                .calculatorFileUuid("attachment")
                .calculatorFileName("filename")
                .build();
        final Set<ConstraintViolation<Cca3FacilityMigrationData>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_zero_totals_valid() {
        final Cca3FacilityMigrationData data = Cca3FacilityMigrationData.builder()
                .accountBusinessId("account")
                .facilityBusinessId("facility")
                .facilityName("name")
                .participatingInCca3Scheme(Boolean.TRUE)
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .energyCarbonFactor(BigDecimal.valueOf(10.999))
                .usedReportingMechanism(Boolean.TRUE)
                .tp7Improvement(BigDecimal.TEN)
                .tp8Improvement(BigDecimal.TEN)
                .tp9Improvement(BigDecimal.TEN)
                .totalFixedEnergy(BigDecimal.ZERO)
                .totalVariableEnergy(BigDecimal.ZERO)
                .totalThroughput(BigDecimal.valueOf(100.999))
                .throughputUnit("unit")
                .calculatorFileUuid("attachment")
                .calculatorFileName("filename")
                .build();
        final Set<ConstraintViolation<Cca3FacilityMigrationData>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_before_2022_not_valid() {
        final Cca3FacilityMigrationData data = Cca3FacilityMigrationData.builder()
                .accountBusinessId("account")
                .facilityBusinessId("facility")
                .facilityName("name")
                .participatingInCca3Scheme(Boolean.TRUE)
                .baselineDate(LocalDate.of(2021, 1, 1))
                .explanation("explanation")
                .measurementType(MeasurementType.ENERGY_KWH)
                .energyCarbonFactor(BigDecimal.valueOf(10.999))
                .usedReportingMechanism(Boolean.TRUE)
                .tp7Improvement(BigDecimal.TEN)
                .tp8Improvement(BigDecimal.TEN)
                .tp9Improvement(BigDecimal.TEN)
                .totalFixedEnergy(BigDecimal.valueOf(100.999))
                .totalVariableEnergy(BigDecimal.ZERO)
                .totalThroughput(BigDecimal.valueOf(100.999))
                .throughputUnit("unit")
                .calculatorFileUuid("attachment")
                .calculatorFileName("filename")
                .build();
        final Set<ConstraintViolation<Cca3FacilityMigrationData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
                .containsExactly("{cca3FacilityMigrationData.baselineDate}");
    }

    @Test
    void validate_no_explanation_not_valid() {
        final Cca3FacilityMigrationData data = Cca3FacilityMigrationData.builder()
                .accountBusinessId("account")
                .facilityBusinessId("facility")
                .facilityName("name")
                .participatingInCca3Scheme(Boolean.TRUE)
                .baselineDate(LocalDate.of(2023, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .energyCarbonFactor(BigDecimal.valueOf(10.999))
                .usedReportingMechanism(Boolean.TRUE)
                .tp7Improvement(BigDecimal.TEN)
                .tp8Improvement(BigDecimal.TEN)
                .tp9Improvement(BigDecimal.TEN)
                .totalFixedEnergy(BigDecimal.valueOf(100.999))
                .totalVariableEnergy(BigDecimal.ZERO)
                .totalThroughput(BigDecimal.valueOf(100.999))
                .throughputUnit("unit")
                .calculatorFileUuid("attachment")
                .calculatorFileName("filename")
                .build();
        final Set<ConstraintViolation<Cca3FacilityMigrationData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
                .containsExactly("{cca3FacilityMigrationData.explanation}");
    }

    @Test
    void validate_with_explanation_valid() {
        final Cca3FacilityMigrationData data = Cca3FacilityMigrationData.builder()
                .accountBusinessId("account")
                .facilityBusinessId("facility")
                .facilityName("name")
                .participatingInCca3Scheme(Boolean.TRUE)
                .baselineDate(LocalDate.of(2023, 1, 1))
                .explanation("explanation")
                .measurementType(MeasurementType.ENERGY_KWH)
                .energyCarbonFactor(BigDecimal.valueOf(10.999))
                .usedReportingMechanism(Boolean.TRUE)
                .tp7Improvement(BigDecimal.TEN)
                .tp8Improvement(BigDecimal.TEN)
                .tp9Improvement(BigDecimal.TEN)
                .totalFixedEnergy(BigDecimal.valueOf(100.999))
                .totalVariableEnergy(BigDecimal.ZERO)
                .totalThroughput(BigDecimal.valueOf(100.999))
                .throughputUnit("unit")
                .calculatorFileUuid("attachment")
                .calculatorFileName("filename")
                .build();
        final Set<ConstraintViolation<Cca3FacilityMigrationData>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_with_scheme_data_not_valid() {
        final Cca3FacilityMigrationData data = Cca3FacilityMigrationData.builder()
                .accountBusinessId("account")
                .facilityBusinessId("facility")
                .facilityName("name")
                .participatingInCca3Scheme(Boolean.FALSE)
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .energyCarbonFactor(BigDecimal.valueOf(10.999))
                .usedReportingMechanism(Boolean.TRUE)
                .tp7Improvement(BigDecimal.TEN)
                .tp8Improvement(BigDecimal.TEN)
                .tp9Improvement(BigDecimal.TEN)
                .totalFixedEnergy(BigDecimal.valueOf(100.999))
                .totalVariableEnergy(BigDecimal.ZERO)
                .totalThroughput(BigDecimal.valueOf(100.999))
                .throughputUnit("unit")
                .calculatorFileUuid("attachment")
                .calculatorFileName("filename")
                .build();
        final Set<ConstraintViolation<Cca3FacilityMigrationData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
                .containsExactly("{cca3FacilityMigrationData.participatingInCca3Scheme}");
    }

    @Test
    void validate_with_scheme_data_optional_not_valid() {
        final Cca3FacilityMigrationData data = Cca3FacilityMigrationData.builder()
                .accountBusinessId("account")
                .facilityBusinessId("facility")
                .facilityName("name")
                .participatingInCca3Scheme(Boolean.FALSE)
                .explanation("explanation")
                .build();
        final Set<ConstraintViolation<Cca3FacilityMigrationData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty().extracting(ConstraintViolation::getMessage)
                .containsExactly("{cca3FacilityMigrationData.explanation}");
    }

    @Test
    void validate_scheme_CCA2_valid() {
        final Cca3FacilityMigrationData data = Cca3FacilityMigrationData.builder()
                .accountBusinessId("account")
                .facilityBusinessId("facility")
                .facilityName("name")
                .participatingInCca3Scheme(Boolean.FALSE)
                .build();
        final Set<ConstraintViolation<Cca3FacilityMigrationData>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_no_values_not_valid() {
        final Cca3FacilityMigrationData data = Cca3FacilityMigrationData.builder().build();
        final Set<ConstraintViolation<Cca3FacilityMigrationData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty().hasSize(5);
    }

    @Test
    void validate_not_all_values_for_CCA3_not_valid() {
        final Cca3FacilityMigrationData data = Cca3FacilityMigrationData.builder()
                .accountBusinessId("account")
                .facilityBusinessId("facility")
                .facilityName("name")
                .participatingInCca3Scheme(Boolean.TRUE)
                .tp7Improvement(BigDecimal.TEN)
                .build();
        final Set<ConstraintViolation<Cca3FacilityMigrationData>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
    }
}
