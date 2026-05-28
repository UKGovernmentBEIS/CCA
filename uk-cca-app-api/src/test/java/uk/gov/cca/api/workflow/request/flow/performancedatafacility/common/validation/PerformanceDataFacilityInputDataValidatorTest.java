package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityProductVariableEnergyData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityInputDataValidatorTest {

    @InjectMocks
    private PerformanceDataFacilityInputDataValidator validator;

    @Mock
    private DataValidator<PerformanceDataFacilityInputData> performanceDataValidator;

    @Test
    void validateData_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder().build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_no_variable_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.TEN)
                        .targetImprovement(BigDecimal.ONE)
                        .adjustedThroughput(BigDecimal.ZERO)
                        .totalTargetVariableEnergy(BigDecimal.ZERO)
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_totals_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.TEN)
                        .targetImprovement(BigDecimal.ONE)
                        .adjustedThroughput(BigDecimal.ZERO)
                        .totalTargetVariableEnergy(BigDecimal.ZERO)
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_by_products_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder().build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.TEN)
                        .throughputAdjustmentFactor(BigDecimal.ONE)
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("name").build()
                        ))
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(true)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder().productName("name").build()
                ))
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_data_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.TEN)
                        .targetImprovement(BigDecimal.ONE)
                        .adjustedThroughput(BigDecimal.ZERO)
                        .totalTargetVariableEnergy(BigDecimal.ZERO)
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.of(new BusinessViolation("", "test")));

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly("test");
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_no_original_srm_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder().build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.TEN)
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("name").build()
                        ))
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder().productName("name").build()
                ))
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_SRM_DATA.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_no_actual_srm_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder().build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("name").build()
                        ))
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(true)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder().productName("name").build()
                ))
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_SRM_DATA.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_CHP_ZERO_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .electricitySuppliedFromCHP(BigDecimal.ZERO)
                        .throughputAdjustmentFactor(BigDecimal.ONE)
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(true)
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_CHP_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .electricitySuppliedFromCHP(BigDecimal.TEN)
                        .throughputAdjustmentFactor(BigDecimal.ONE)
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(true)
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_CHP_DATA.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_energy_data_no_variable_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_VARIABLE_ENERGY_DATA.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_energy_data_totals_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.TEN)
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("name").build()
                        ))
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactlyInAnyOrder(
                PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.INVALID_VARIABLE_ENERGY_DATA.getMessage(),
                PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.INVALID_PRODUCTS.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_energy_data_totals_no_actual_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_VARIABLE_ENERGY_DATA.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_energy_data_by_products_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.TEN)
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("name").build()
                        ))
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder().productName("name").build()
                ))
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_VARIABLE_ENERGY_DATA.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_energy_data_by_products_no_products_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder().productName("name").build()
                ))
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_VARIABLE_ENERGY_DATA.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_missing_original_product_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("name").build()
                        ))
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder().productName("name").build(),
                        ProductVariableEnergyConsumptionData.builder().productName("name2").build()
                ))
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_PRODUCTS.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_extra_actual_product_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("name").build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("name2").build()
                        ))
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder().productName("name").build()
                ))
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .INVALID_PRODUCTS.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    @Test
    void validateData_duplicate_product_not_valid() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("name").build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("name").build()
                        ))
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .usedReportingMechanism(false)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder().productName("name").build()
                ))
                .build();

        when(performanceDataValidator.validate(performanceData))
                .thenReturn(Optional.empty());

        // Invoke
        List<BusinessValidationResult> result = validator.validateData(performanceData, calculationParameters);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        assertThat(getViolations(result)).containsExactly(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage
                .DUPLICATE_PRODUCTS_EXISTS.getMessage());
        verify(performanceDataValidator, times(1)).validate(performanceData);
    }

    private List<String> getViolations(List<BusinessValidationResult> validationResults) {
        return validationResults.stream().map(BusinessValidationResult::getViolations)
                .flatMap(List::stream).map(violation -> Arrays.stream(violation.getData()).toList())
                .flatMap(List::stream).map(Object::toString).toList();
    }
}
