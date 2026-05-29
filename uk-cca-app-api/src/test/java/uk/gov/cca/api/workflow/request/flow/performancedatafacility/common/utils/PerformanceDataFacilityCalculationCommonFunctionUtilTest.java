package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
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
}
