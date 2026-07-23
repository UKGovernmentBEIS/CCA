package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityProductVariableEnergyData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityCalculationCommonFunctionUtilTest {

    @Test
    void test_PRODUCT_TARGET_IMPROVEMENT_negative_progressAtProductBaseYear() {
        final PerformanceDataFacilityCalculationParameters parameters = PerformanceDataFacilityCalculationParameters.builder()
                .baselineDate(LocalDate.of(2022, 1, 1))
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(10),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .targetImprovement(BigDecimal.valueOf(0.08))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final ProductVariableEnergyConsumptionData originalProduct = ProductVariableEnergyConsumptionData.builder()
                .baselineYear(Year.of(2027))
                .build();

        // Invoke
        BigDecimal productTargetImprovement = PerformanceDataFacilityCalculationCommonFunctionUtil.PRODUCT_TARGET_IMPROVEMENT
                .apply(parameters, originalProduct).setScale(7, RoundingMode.HALF_UP);

        // Verify
        assertThat(productTargetImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
    }

    @Test
    void test_PRODUCT_TARGET_IMPROVEMENT() {
        final PerformanceDataFacilityCalculationParameters parameters = PerformanceDataFacilityCalculationParameters.builder()
                .baselineDate(LocalDate.of(2022, 1, 1))
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(15),
                        TargetImprovementType.TP8, BigDecimal.valueOf(10),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .targetImprovement(BigDecimal.valueOf(0.15))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final ProductVariableEnergyConsumptionData originalProduct = ProductVariableEnergyConsumptionData.builder()
                .baselineYear(Year.of(2027))
                .build();

        // Invoke
        BigDecimal productTargetImprovement = PerformanceDataFacilityCalculationCommonFunctionUtil.PRODUCT_TARGET_IMPROVEMENT
                .apply(parameters, originalProduct).setScale(7, RoundingMode.HALF_UP);

        // Verify
        assertThat(productTargetImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.0285714).setScale(7, RoundingMode.HALF_UP));
    }

    @Test
    void test_PRODUCT_TARGET_IMPROVEMENT_TP8() {
        final PerformanceDataFacilityCalculationParameters parameters = PerformanceDataFacilityCalculationParameters.builder()
                .baselineDate(LocalDate.of(2022, 1, 1))
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(10),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .targetImprovement(BigDecimal.valueOf(0.1))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final ProductVariableEnergyConsumptionData originalProduct = ProductVariableEnergyConsumptionData.builder()
                .baselineYear(Year.of(2027))
                .build();

        // Invoke
        BigDecimal productTargetImprovement = PerformanceDataFacilityCalculationCommonFunctionUtil.PRODUCT_TARGET_IMPROVEMENT
                .apply(parameters, originalProduct).setScale(7, RoundingMode.HALF_UP);

        // Verify
        assertThat(productTargetImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.010989).setScale(7, RoundingMode.HALF_UP));
    }
    
    @Test
    void test_TOTAL_TARGET_VARIABLE_ENERGY() {
        final PerformanceDataFacilityCalculationParameters parameters = PerformanceDataFacilityCalculationParameters.builder()
                .baselineDate(LocalDate.of(2022, 1, 1))
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .usedReportingMechanism(true)
                .measurementType(MeasurementType.ENERGY_KWH)
                .totalFixedEnergy(BigDecimal.ZERO)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .targetImprovement(BigDecimal.valueOf(0.08))
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
        
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(10000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(500000)).build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(1500000))
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Low EI product").actualThroughput(BigDecimal.valueOf(6000)).build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Hi EI product").actualThroughput(BigDecimal.valueOf(4000)).build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("New product").actualThroughput(BigDecimal.valueOf(500)).build()
                        ))
                        .build())
                .build();


        // Invoke
        BigDecimal totalTargetVariableEnergy = PerformanceDataFacilityCalculationCommonFunctionUtil.TOTAL_TARGET_VARIABLE_ENERGY
                .apply(parameters, data).setScale(7, RoundingMode.HALF_UP);

        // Verify
        assertThat(totalTargetVariableEnergy.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30211968.0851064).setScale(7, RoundingMode.HALF_UP));
    }
}
