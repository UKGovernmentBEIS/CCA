package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityProductVariableEnergyData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormInputCalculatedDataValidatorTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormInputCalculatedDataValidator validator;

    @Test
    void validateInputCalculatedData_FIXED() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(1000000))
                                        .primaryEnergy(BigDecimal.valueOf(1000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(2000000))
                                        .primaryEnergy(BigDecimal.valueOf(2000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(500000))
                                        .primaryEnergy(BigDecimal.valueOf(500000))
                                        .build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(1500000))
                                        .conversionFactor(BigDecimal.ZERO)
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6875))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .targetImprovement(BigDecimal.valueOf(0.08))
                        .adjustedThroughput(BigDecimal.valueOf(7218.75))
                        .totalTargetVariableEnergy(BigDecimal.ZERO)
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(30000000))
                .build();

        // Invoke
        List<BusinessValidationResult> results = validator.validateInputCalculatedData(performanceData, calculationParameters);

        // Verify
        assertThat(results.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
    }

    @Test
    void validateInputCalculatedData_FIXED_no_SRM() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(1000000))
                                        .primaryEnergy(BigDecimal.valueOf(1000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(2000000))
                                        .primaryEnergy(BigDecimal.valueOf(2000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(500000))
                                        .primaryEnergy(BigDecimal.valueOf(500000))
                                        .build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(1500000))
                                        .conversionFactor(BigDecimal.ZERO)
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .targetImprovement(BigDecimal.valueOf(0.08))
                        .adjustedThroughput(BigDecimal.valueOf(10500))
                        .totalTargetVariableEnergy(BigDecimal.ZERO)
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(false)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(30000000))
                .build();

        // Invoke
        List<BusinessValidationResult> results = validator.validateInputCalculatedData(performanceData, calculationParameters);

        // Verify
        assertThat(results.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
    }

    @Test
    void validateInputCalculatedData_TOTALS() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(1000000))
                                        .primaryEnergy(BigDecimal.valueOf(1000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(2000000))
                                        .primaryEnergy(BigDecimal.valueOf(2000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(500000))
                                        .primaryEnergy(BigDecimal.valueOf(500000))
                                        .build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(1500000))
                                        .conversionFactor(BigDecimal.ZERO)
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6875))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .targetImprovement(BigDecimal.valueOf(0.08))
                        .adjustedThroughput(BigDecimal.valueOf(7218.75))
                        .totalTargetVariableEnergy(BigDecimal.valueOf(19923750))
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.ZERO)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(30000000))
                .totalThroughput(BigDecimal.valueOf(10000))
                .build();

        // Invoke
        List<BusinessValidationResult> results = validator.validateInputCalculatedData(performanceData, calculationParameters);

        // Verify
        assertThat(results.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
    }

    @Test
    void validateInputCalculatedData_PRODUCTS() {
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(1000000))
                                        .primaryEnergy(BigDecimal.valueOf(1000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(2000000))
                                        .primaryEnergy(BigDecimal.valueOf(2000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(500000))
                                        .primaryEnergy(BigDecimal.valueOf(500000))
                                        .build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(1500000))
                                        .conversionFactor(BigDecimal.ZERO)
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6875))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .totalTargetVariableEnergy(BigDecimal.valueOf(30211968.0851064))
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("Low EI product")
                                        .targetImprovement(BigDecimal.valueOf(0.08))
                                        .actualThroughput(BigDecimal.valueOf(6000))
                                        .adjustedThroughput(BigDecimal.valueOf(4125))
                                        .targetEnergy(BigDecimal.valueOf(4743750))
                                        .build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("Hi EI product")
                                        .targetImprovement(BigDecimal.valueOf(0.08))
                                        .actualThroughput(BigDecimal.valueOf(4000))
                                        .adjustedThroughput(BigDecimal.valueOf(2750))
                                        .targetEnergy(BigDecimal.valueOf(25300000))
                                        .build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("New product")
                                        .targetImprovement(BigDecimal.valueOf(0.0212766))
                                        .actualThroughput(BigDecimal.valueOf(500))
                                        .adjustedThroughput(BigDecimal.valueOf(343.75))
                                        .targetEnergy(BigDecimal.valueOf(168218.0851064))
                                        .build()
                        ))
                        .build())
                .build();
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.ZERO)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(10000000))
                                .throughput(BigDecimal.valueOf(8000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(20000000))
                                .throughput(BigDecimal.valueOf(2000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("New product")
                                .baselineYear(Year.of(2025))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(1000000))
                                .throughput(BigDecimal.valueOf(2000))
                                .throughputUnit("units")
                                .build()
                ))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        // Invoke
        List<BusinessValidationResult> results = validator.validateInputCalculatedData(performanceData, calculationParameters);

        // Verify
        assertThat(results.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
    }
}
