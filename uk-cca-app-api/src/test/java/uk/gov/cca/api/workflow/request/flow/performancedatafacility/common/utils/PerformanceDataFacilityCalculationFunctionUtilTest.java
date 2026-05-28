package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
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
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <a href="https://trasys.atlassian.net/wiki/spaces/CCA/pages/882376705/CCA3+-+TPR+Interim+Reporting+Data+Model">Confluence</a>
 *  Test with Excels: CCA3 target period reporting methodology (worked examples).xlsx, Carbon dioxide example.xlsx
 *  Excel values:
 *  actualEnergyCarbon: L24
 *  targetEnergyCarbon: L21
 *  energyCarbonDifference: L25
 *  weightedConversionFactor: "Fuel Consumed" B35
 *  targetCo2Emissions: No representation
 *  actualCo2Emissions: L31
 *  co2EmissionsDifference: L26
 *  actualImprovement: L28
 *  targetPeriodResultType: No representation
 *  surplusGained: L30
 *  buyOutRequired: No representation
 */
@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityCalculationFunctionUtilTest {

    @Test
    void test_ENERGY_KWH_Fixed() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(27600000).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3400000).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.115475322580645).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3187.1189032).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(392.616096774194).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.03333333).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(393));
    }

    @Test
    void test_ENERGY_KWH_Fixed_no_SRM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(27600000).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3400000).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.115475322580645).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3187.1189032).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(392.616096774194).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.03333333).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(393));
    }

    @Test
    void test_ENERGY_KWH_Fixed_TP8() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2028))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.12))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(30000000))
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(52800000).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-21800000).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.115475322580645).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(6097.0970323).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.7350000).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-2517.362032258060000).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.483333333333333).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.valueOf(2517));
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_ENERGY_KWH_Fixed_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.1))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(30000000))
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(54000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-23000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.115475322580645).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(6235.6674194).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.7350000).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-2655.93241935484).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.483333333333333).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.valueOf(2655));
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_ENERGY_KWH_Fixed_zeros() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.ZERO)
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(false)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.ZERO,
                        TargetImprovementType.TP8, BigDecimal.ZERO,
                        TargetImprovementType.TP9, BigDecimal.ZERO
                ))
                .totalFixedEnergy(BigDecimal.ZERO)
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_ENERGY_KWH_Variable_TOTAL() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(19923750).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(11076250).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2300.7014583).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1279.03354173387).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.431457431457432).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(1280));
    }

    @Test
    void test_ENERGY_KWH_Variable_TOTAL_no_SRM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
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
                .totalFixedEnergy(BigDecimal.ZERO)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(30000000))
                .totalThroughput(BigDecimal.valueOf(10000))
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(28980000).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2020000).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3346.4748484).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(233.260151612903).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.0158730158730159).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(234));
    }

    @Test
    void test_ENERGY_KWH_Variable_TOTAL_TP8() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2028))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.12))
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(19057500).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(11942500).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2200.6709601).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1379.06403991935).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.431457431457432).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(1380));
    }

    @Test
    void test_ENERGY_KWH_Variable_TOTAL_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.1))
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(19490625).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(11509375).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2250.6862092).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1329.04879082661).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.431457431457432).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(1330));
    }

    @Test
    void test_ENERGY_KWH_Variable_TOTAL_zeros() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.ZERO)
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(false)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.ZERO,
                        TargetImprovementType.TP8, BigDecimal.ZERO,
                        TargetImprovementType.TP9, BigDecimal.ZERO
                ))
                .totalFixedEnergy(BigDecimal.ZERO)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.ZERO)
                .totalThroughput(BigDecimal.valueOf(10000))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_ENERGY_KWH_Variable_PRODUCTS() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
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
                                        .conversionFactor(BigDecimal.ZERO)
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
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30211968.0851064).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(788031.914893616).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3488.7367604).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(90.9982395761838).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.0556877677296526).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(91));
    }

    @Test
    void test_ENERGY_KWH_Variable_PRODUCTS_no_SRM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
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
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(43944680.8510638).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-12944680.8510638).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(5074.5261970).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-1494.7911969801000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.350785340314136).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.valueOf(1494));
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_ENERGY_KWH_Variable_PRODUCTS_TP8() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2028))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.12))
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
                                        .conversionFactor(BigDecimal.ZERO)
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
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(28898404.255319100).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2101595.74468085).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3337.0525534).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(242.682446551133).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.0556877677296526).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(243));
    }

    @Test
    void test_ENERGY_KWH_Variable_PRODUCTS_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.1))
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
                                        .conversionFactor(BigDecimal.ZERO)
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
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(29555186.1702128).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1444813.82978724).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3412.8946569).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(166.840343063658).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.0556877677296526).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(167));
    }

    @Test
    void test_ENERGY_KWH_Variable_PRODUCTS_zeros() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.ZERO)
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(false)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.ZERO,
                        TargetImprovementType.TP8, BigDecimal.ZERO,
                        TargetImprovementType.TP9, BigDecimal.ZERO
                ))
                .totalFixedEnergy(BigDecimal.ZERO)
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.ZERO)
                                .throughput(BigDecimal.valueOf(8000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.ZERO)
                                .throughput(BigDecimal.valueOf(2000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("New product")
                                .baselineYear(Year.of(2025))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.ZERO)
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
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Low EI product").actualThroughput(BigDecimal.valueOf(6000)).build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Hi EI product").actualThroughput(BigDecimal.valueOf(4000)).build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("New product").actualThroughput(BigDecimal.valueOf(500)).build()
                        ))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_ENERGY_KWH_fixed_TOTAL() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
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
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(20000000))
                .totalThroughput(BigDecimal.valueOf(10000))
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(22482500).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(8517500).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2596.1739399).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(983.561060080645).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.268542199488491).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(984));
    }

    @Test
    void test_ENERGY_KWH_fixed_TOTAL_no_SRM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
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
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(20000000))
                .totalThroughput(BigDecimal.valueOf(10000))
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(28520000).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2480000).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3293.3562).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(286.3788).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(287));
    }

    @Test
    void test_ENERGY_KWH_fixed_TOTAL_TP8() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2028))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.12))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(20000000))
                .totalThroughput(BigDecimal.valueOf(10000))
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30305000).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(695000).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3499.4796508).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(80.2553491935484).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.0998185117967332).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(81));
    }

    @Test
    void test_ENERGY_KWH_fixed_TOTAL_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.1))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(20000000))
                .totalThroughput(BigDecimal.valueOf(10000))
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30993750).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(6250).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.0132792).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.721720766129032).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.0998185117967332).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(1));
    }

    @Test
    void test_ENERGY_KWH_fixed_PRODUCTS() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
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
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(5000000))
                                .throughput(BigDecimal.valueOf(8000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(15000000))
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
                                        .conversionFactor(BigDecimal.ZERO)
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
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30715093.0851064).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(284906.914893616).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3546.8352821).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(32.8997179027967).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.0711610486891385).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.valueOf(33));
    }

    @Test
    void test_ENERGY_KWH_fixed_PRODUCTS_no_SRM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
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
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(5000000))
                                .throughput(BigDecimal.valueOf(8000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(15000000))
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
                                        .conversionFactor(BigDecimal.ZERO)
                                        .build()
                        ))
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
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(40494680.8510638).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-9494680.85106383).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(4676.1363341).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-1096.40133407687).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.295454545454545).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.valueOf(1096));
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_ENERGY_KWH_fixed_PRODUCTS_TP8() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2028))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.12))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(5000000))
                                .throughput(BigDecimal.valueOf(8000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(15000000))
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
                                        .conversionFactor(BigDecimal.ZERO)
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
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(38179654.2553191).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-7179654.2553191).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(4408.8078912).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-829.07289115048).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.285302593659942).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.valueOf(829));
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_ENERGY_KWH_fixed_PRODUCTS_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.1))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(5000000))
                                .throughput(BigDecimal.valueOf(8000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(15000000))
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
                                        .conversionFactor(BigDecimal.ZERO)
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
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(39047373.6702128).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-8047373.670212770).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(4509.0080705).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-929.27307049481).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.285302593659942).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.valueOf(929));
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_CARBON_KG_fixed_TOTAL() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.CARBON_KG)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(100000))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(800000))
                .totalThroughput(BigDecimal.valueOf(5000))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1800000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5678)).build(),
                                PerformanceDataFacilityFixedConversionFactor.PETROL, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2145)).build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Other Fuel 01")
                                        .deliveredEnergy(BigDecimal.valueOf(90000))
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(100000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(5000))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755565.39677).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(802620.689655173).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-47055.2928851725).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1403856).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(802.6206896552).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755.56539677).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-47.0552928851725).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.133936896983004).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.valueOf(47));
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_CARBON_KG_fixed_TOTAL_no_SRM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.CARBON_KG)
                .usedReportingMechanism(false)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(100000))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(800000))
                .totalThroughput(BigDecimal.valueOf(5000))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1800000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5678)).build(),
                                PerformanceDataFacilityFixedConversionFactor.PETROL, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2145)).build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Other Fuel 01")
                                        .deliveredEnergy(BigDecimal.valueOf(90000))
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(5000))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755565.39677).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(828000).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-72434.60323).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1403856).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(828).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755.56539677).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-72.4346032).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.160482892477778).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.valueOf(72));
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_CARBON_KG_fixed_TOTAL_TP8() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2028))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.12))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.CARBON_KG)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(100000))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(800000))
                .totalThroughput(BigDecimal.valueOf(5000))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1800000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5678)).build(),
                                PerformanceDataFacilityFixedConversionFactor.PETROL, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2145)).build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Other Fuel 01")
                                        .deliveredEnergy(BigDecimal.valueOf(90000))
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(100000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(5000))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755565.39677).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(855724.137931035).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-100158.741161035).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1403856).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(855.7241379).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755.56539677).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-100.158741161035).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.223000123889007).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.valueOf(100));
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_CARBON_KG_fixed_TOTAL_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.1))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.CARBON_KG)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(100000))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(800000))
                .totalThroughput(BigDecimal.valueOf(5000))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1800000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5678)).build(),
                                PerformanceDataFacilityFixedConversionFactor.PETROL, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2145)).build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Other Fuel 01")
                                        .deliveredEnergy(BigDecimal.valueOf(90000))
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(100000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(5000))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755565.39677).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(875172.413793103).setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-119607.017023103).setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1403856).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(875.1724138).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755.56539677).setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-119.607017023103).setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.223000123889007).setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.valueOf(119));
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_CARBON_KG_fixed_TOTAL_zeros() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.ZERO)
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.CARBON_KG)
                .usedReportingMechanism(false)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.ZERO,
                        TargetImprovementType.TP8, BigDecimal.ZERO,
                        TargetImprovementType.TP9, BigDecimal.ZERO
                ))
                .totalFixedEnergy(BigDecimal.ZERO)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.ZERO)
                .totalThroughput(BigDecimal.valueOf(5000))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(5000))
                        .build())
                .build();

        // Invoke
        BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
                .apply(calculatedInfoData, data);
        BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
                .apply(actualEnergyCarbon, targetEnergyCarbon);
        BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
                .apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
        BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_CO2_EMISSIONS
                .apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
        BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_CO2_EMISSIONS
                .apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
        BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.CO2_EMISSIONS_DIFFERENCE
                .apply(actualCo2Emissions, targetCo2Emissions);
        BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
                .apply(actualEnergyCarbon, calculatedInfoData, data);
        PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
                .apply(energyCarbonDifference);
        BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED.apply(co2EmissionsDifference);
        BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED.apply(co2EmissionsDifference);

        // Verify
        assertThat(actualEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetEnergyCarbon.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(energyCarbonDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(weightedConversionFactor.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(actualCo2Emissions.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(co2EmissionsDifference.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(actualImprovement.setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(targetPeriodResultType).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(surplusGained).isEqualTo(BigDecimal.ZERO);
        assertThat(buyOutRequired).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_INTERIM_TARGET() {
        final Map<TargetImprovementType, BigDecimal> improvements = Map.of(
                TargetImprovementType.TP7, BigDecimal.valueOf(0.08),
                TargetImprovementType.TP8, BigDecimal.valueOf(0.12),
                TargetImprovementType.TP9, BigDecimal.valueOf(0.16)
        );
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP8;

        // Invoke
        BigDecimal result = PerformanceDataFacilityCalculationFunctionUtil.INTERIM_TARGET
                .apply(improvements, targetPeriodType).setScale(7, RoundingMode.HALF_UP);

        // Verify
        assertThat(result).isEqualTo(BigDecimal.valueOf(0.1).setScale(7, RoundingMode.HALF_UP));
    }

    @Test
    void test_INTERIM_TARGET_no_previous() {
        final Map<TargetImprovementType, BigDecimal> improvements = Map.of(
                TargetImprovementType.TP7, BigDecimal.ONE,
                TargetImprovementType.TP8, BigDecimal.TWO,
                TargetImprovementType.TP9, BigDecimal.TEN
        );
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

        // Invoke
        BigDecimal result = PerformanceDataFacilityCalculationFunctionUtil.INTERIM_TARGET
                .apply(improvements, targetPeriodType).setScale(7, RoundingMode.HALF_UP);

        // Verify
        assertThat(result).isEqualTo(BigDecimal.valueOf(0.5).setScale(7, RoundingMode.HALF_UP));
    }
}
