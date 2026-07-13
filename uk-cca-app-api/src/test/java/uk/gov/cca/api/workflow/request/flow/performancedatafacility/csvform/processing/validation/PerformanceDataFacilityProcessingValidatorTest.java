package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityUploadCsvData;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityProcessingValidatorTest {

    @InjectMocks
    private PerformanceDataFacilityProcessingValidator validator;

    @Test
    void validateCsvRules() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .naturalGas(BigDecimal.ONE)
                .productActualThroughput1(BigDecimal.ONE)
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .build();

        // Invoke
        BusinessValidationResult result = validator.validateCsvRules(csvData, calculationParameters);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateCsvRules_no_fuels_no_products() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder().build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .build();

        // Invoke
        BusinessValidationResult result = validator.validateCsvRules(csvData, calculationParameters);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataFacilityViolation>) result.getViolations()).extracting(PerformanceDataFacilityViolation::getMessage)
                .containsExactlyInAnyOrder(
                        PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_CSV_FUELS_NOT_VALID.getMessage(),
                        PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_CSV_PRODUCTS_NOT_VALID.getMessage());
    }
}
