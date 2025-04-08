package uk.gov.cca.api.underlyingagreement.domain.baselinetargets;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TargetCompositionTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_no_throughput_units_valid() {
        final TargetComposition data = TargetComposition.builder()
                .calculatorFile(UUID.randomUUID())
                .measurementType(MeasurementType.ENERGY_GJ)
                .agreementCompositionType(AgreementCompositionType.NOVEM)
                .build();
        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_no_isTargetUnitThroughputMeasured_not_valid() {
        final TargetComposition data = TargetComposition.builder()
                .calculatorFile(UUID.randomUUID())
                .measurementType(MeasurementType.ENERGY_GJ)
                .agreementCompositionType(AgreementCompositionType.NOVEM)
                .isTargetUnitThroughputMeasured(Boolean.FALSE)
                .build();
        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.targetComposition.isTargetUnitThroughputMeasured}");
    }

    @Test
    void validate_no_throughput_units_not_valid() {
        final TargetComposition data = TargetComposition.builder()
                .calculatorFile(UUID.randomUUID())
                .measurementType(MeasurementType.ENERGY_GJ)
                .agreementCompositionType(AgreementCompositionType.NOVEM)
                .isTargetUnitThroughputMeasured(Boolean.TRUE)
                .throughputUnit("kg")
                .conversionFactor(BigDecimal.valueOf(10.4111))
                .conversionEvidences(Set.of(UUID.randomUUID()))
                .build();
        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{underlyingagreement.targetComposition.isTargetUnitThroughputMeasured}",
                        "{underlyingagreement.targetComposition.throughputUnit}",
                        "{underlyingagreement.targetComposition.conversionFactor}",
                        "{underlyingagreement.targetComposition.conversionEvidences}");
    }

    @Test
    void validate_with_throughput_units_valid() {
        final TargetComposition data = TargetComposition.builder()
                .calculatorFile(UUID.randomUUID())
                .measurementType(MeasurementType.ENERGY_GJ)
                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                .isTargetUnitThroughputMeasured(Boolean.TRUE)
                .throughputUnit("kg")
                .conversionFactor(BigDecimal.valueOf(1.111))
                .conversionEvidences(Set.of(UUID.randomUUID()))
                .build();
        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }
    @Test
    void validate_with_throughput_units_without_conversionFactor_valid() {
        final TargetComposition data = TargetComposition.builder()
                .calculatorFile(UUID.randomUUID())
                .measurementType(MeasurementType.ENERGY_GJ)
                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                .isTargetUnitThroughputMeasured(Boolean.TRUE)
                .throughputUnit("kg")
                .conversionFactor(null)
                .conversionEvidences(Set.of(UUID.randomUUID()))
                .build();
        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_with_throughput_units_not_measured_invalid() {
        final TargetComposition data = TargetComposition.builder()
                .calculatorFile(UUID.randomUUID())
                .measurementType(MeasurementType.ENERGY_GJ)
                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                .isTargetUnitThroughputMeasured(Boolean.FALSE)
                .conversionFactor(BigDecimal.valueOf(1.111))
                .build();
        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsOnly(
                        "{underlyingagreement.targetComposition.conversionFactor}");
    }

    @Test
    void validate_with_throughput_units_no_targetUnitThroughputMeasured_valid() {
        final TargetComposition data = TargetComposition.builder()
                .calculatorFile(UUID.randomUUID())
                .measurementType(MeasurementType.ENERGY_GJ)
                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                .isTargetUnitThroughputMeasured(Boolean.FALSE)
                .conversionFactor(null)
                .build();
        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_with_throughput_units_no_targetUnitThroughputMeasured_not_valid() {
        final TargetComposition data = TargetComposition.builder()
                .calculatorFile(UUID.randomUUID())
                .measurementType(MeasurementType.ENERGY_GJ)
                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                .isTargetUnitThroughputMeasured(Boolean.FALSE)
                .throughputUnit("kg")
                .conversionFactor(BigDecimal.valueOf(0.1555))
                .conversionEvidences(Set.of(UUID.randomUUID()))
                .build();
        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "{underlyingagreement.targetComposition.conversionFactor}",
                        "{underlyingagreement.targetComposition.conversionEvidences}");
    }

    @Test
    void validate_with_throughput_units_conversionFactor_invalid() {
        final TargetComposition data = TargetComposition.builder()
                .calculatorFile(UUID.randomUUID())
                .measurementType(MeasurementType.ENERGY_GJ)
                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                .isTargetUnitThroughputMeasured(Boolean.TRUE)
                .throughputUnit("GJ")
                .conversionFactor(BigDecimal.valueOf(0.155511111))
                .conversionEvidences(Set.of(UUID.randomUUID()))
                .build();

        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("numeric value out of bounds (<2147483647 digits>.<7 digits> expected)");
    }

    @Test
    void validate_with_throughput_units_conversionFactor_valid() {
        final TargetComposition data = TargetComposition.builder()
                .calculatorFile(UUID.randomUUID())
                .measurementType(MeasurementType.ENERGY_GJ)
                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                .isTargetUnitThroughputMeasured(Boolean.TRUE)
                .throughputUnit("GJ")
                .conversionFactor(BigDecimal.valueOf(0.1555))
                .conversionEvidences(Set.of(UUID.randomUUID()))
                .build();
        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_no_data_not_valid() {
        final TargetComposition data = TargetComposition.builder().build();
        final Set<ConstraintViolation<TargetComposition>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty().hasSize(7);
    }
}
