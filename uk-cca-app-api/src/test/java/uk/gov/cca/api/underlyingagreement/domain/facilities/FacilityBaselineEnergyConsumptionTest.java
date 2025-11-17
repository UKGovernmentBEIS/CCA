package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.underlyingagreement.domain.facilities.ProductStatus.NEW;

class FacilityBaselineEnergyConsumptionTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_data_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(-1258.63))
                .totalThroughput(BigDecimal.valueOf(12.63))
                .throughputUnit("Each")
                .variableEnergyConsumptionDataByProduct(Collections.emptyList())
                .build();


        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_data_with_false_hasVariableEnergy_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(false)
                .totalThroughput(BigDecimal.valueOf(256.63))
                .throughputUnit("Each")
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_zero_energy_with_products_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(null)
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(BigDecimal.ZERO)
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each")
                        .productStatus(NEW)
                        .baselineYear(Year.of(2022))
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_variable_energy_with_totalThroughput_and_throughputUnit_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(BigDecimal.valueOf(12.63))
                .totalThroughput(BigDecimal.valueOf(125.6))
                .throughputUnit("Each")
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(BigDecimal.ZERO)
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each").productStatus(NEW)
                        .baselineYear(Year.of(2022))
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{underlyingagreement.facilities.facilityEnergyConsumption.variableEnergyType}");
    }

    @Test
    void validate_variable_energy_without_totalThroughput_and_throughputUnit_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(12.63))
                .totalThroughput(null)
                .throughputUnit(null)
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{underlyingagreement.facilities.facilityEnergyConsumption.variableEnergyType}");
    }

    @Test
    void validate_no_variable_energy_without_energy_type_with_energy_data_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(false)
                .variableEnergyType(null)
                .baselineVariableEnergy(null)
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(BigDecimal.ZERO)
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each").productStatus(NEW)
                        .baselineYear(Year.of(2022))
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{underlyingagreement.facilities.facilityEnergyConsumption.hasVariableEnergy}");
    }

    @Test
    void validate_with_hasVariableEnergy_and_variableEnergyType_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(false)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(BigDecimal.valueOf(256))
                .totalThroughput(BigDecimal.valueOf(256.63))
                .throughputUnit("Each")
                .variableEnergyConsumptionDataByProduct(Collections.emptyList())
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{underlyingagreement.facilities.facilityEnergyConsumption.variableEnergyType}",
                        "{underlyingagreement.facilities.facilityEnergyConsumption.hasVariableEnergy}");
    }

    @Test
    void validate_by_product_with_null_energy_data_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(null)
                .totalThroughput(null)
                .throughputUnit(null)
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .productStatus(NEW)
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each")
                        .baselineYear(Year.of(2022))
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void validate_hasVariableEnergy_null_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(null)
                .variableEnergyType(null)
                .baselineVariableEnergy(null)
                .totalThroughput(BigDecimal.valueOf(12.63))
                .throughputUnit("Each")
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(BigDecimal.ZERO)
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each")
                        .productStatus(NEW)
                        .baselineYear(Year.of(2022))
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void validate_has_variable_energy_with_no_variable_energy_type_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(null)
                .baselineVariableEnergy(null)
                .totalThroughput(BigDecimal.valueOf(12.63))
                .throughputUnit("Each")
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(BigDecimal.valueOf(85.69))
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each").productStatus(NEW)
                        .baselineYear(Year.of(2022))
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.facilityEnergyConsumption.hasVariableEnergy}");
    }

    @Test
    void validate_variable_energy_type_totals_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(null)
                .totalThroughput(BigDecimal.valueOf(12.63))
                .throughputUnit("Each")
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(BigDecimal.valueOf(55.69))
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each").productStatus(NEW)
                        .baselineYear(Year.of(2022))
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.facilityEnergyConsumption.variableEnergyType}");
    }

    @Test
    void validate_variable_empty_energy_data_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(null)
                .totalThroughput(null)
                .throughputUnit(null)
                .variableEnergyConsumptionDataByProduct(null)
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{underlyingagreement.facilities.facilityEnergyConsumption.hasVariableEnergy}",
                        "{underlyingagreement.facilities.facilityEnergyConsumption.variableEnergyType}");
    }

    @Test
    void validate_has_variable_energy_and_variable_energy_type_by_product_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .baselineVariableEnergy(null)
                .totalThroughput(BigDecimal.valueOf(12.63))
                .throughputUnit("Each")
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(Collections.emptyList())
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{underlyingagreement.facilities.facilityEnergyConsumption.hasVariableEnergy}",
                        "{underlyingagreement.facilities.facilityEnergyConsumption.variableEnergyType}");
    }

    @Test
    void validate_variable_energy_type_by_product_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(BigDecimal.valueOf(55.69))
                .totalThroughput(BigDecimal.valueOf(12.63))
                .throughputUnit("Each").variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(BigDecimal.valueOf(55.69))
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each")
                        .productStatus(NEW)
                        .baselineYear(Year.of(2022))
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.facilityEnergyConsumption.variableEnergyType}");
    }

    @Test
    void validate_variable_energy_by_product_baseline_year_2021_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .baselineVariableEnergy(null)
                .totalThroughput(null)
                .throughputUnit(null)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(BigDecimal.valueOf(85.69))
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each").productStatus(NEW)
                        .baselineYear(Year.of(2021))
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.facilityEnergyConsumption.ProductVariableEnergyConsumptionData.baselineYear}");
    }

    @Test
    void validate_variable_energy_by_product_baseline_year_2031_not_valid() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(BigDecimal.valueOf(85.69))
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each").productStatus(NEW)
                        .baselineYear(Year.of(2031))
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.facilityEnergyConsumption.ProductVariableEnergyConsumptionData.baselineYear}");
    }

    @Test
    void validate_variable_energy_by_product_baseline_year_not_valid_3() {
        FacilityBaselineEnergyConsumption data = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(BigDecimal.valueOf(85.69))
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each")
                        .baselineYear(null)
                        .productStatus(NEW)
                        .productName("Product1")
                        .build()))
                .build();

        final Set<ConstraintViolation<FacilityBaselineEnergyConsumption>> violations = validator.validate(data);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void getTotalBaselineVariableEnergy() {
        final BigDecimal productVariableEnergy = BigDecimal.valueOf(555);
        final FacilityBaselineEnergyConsumption facilityBaselineEnergyConsumption = FacilityBaselineEnergyConsumption.builder()
                .totalFixedEnergy(BigDecimal.ZERO)
                .hasVariableEnergy(true)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(null)
                .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                        .energy(productVariableEnergy)
                        .throughput(BigDecimal.valueOf(12.63))
                        .throughputUnit("Each")
                        .productStatus(NEW)
                        .baselineYear(Year.of(2022))
                        .productName("Product1")
                        .build()))
                .build();

        LocalDate baselineStartDate = LocalDate.of(2022, 1, 1);
        Optional<BigDecimal> totalBaselineVariableEnergy =
                facilityBaselineEnergyConsumption.getTotalBaselineVariableEnergy(baselineStartDate);

        assertThat(totalBaselineVariableEnergy).isPresent();
        assertThat(totalBaselineVariableEnergy.get()).isEqualByComparingTo(productVariableEnergy);

        // check that in the non-leap year (2022) the baseline start year is 2023 (2022+1)
        // with no products summing to ZERO
        baselineStartDate = LocalDate.of(2022, 7, 3);
        totalBaselineVariableEnergy  =
                facilityBaselineEnergyConsumption.getTotalBaselineVariableEnergy(baselineStartDate);

        assertThat(totalBaselineVariableEnergy).isPresent();
        assertThat(totalBaselineVariableEnergy.get()).isEqualTo(BigDecimal.ZERO);
    }
}
