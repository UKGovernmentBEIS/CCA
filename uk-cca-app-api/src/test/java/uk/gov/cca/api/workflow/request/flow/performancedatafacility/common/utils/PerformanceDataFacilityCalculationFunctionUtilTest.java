package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityProductVariableEnergyData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;

/**
 * <a href="https://trasys.atlassian.net/wiki/spaces/CCA/pages/882376705/CCA3+-+TPR+Interim+Reporting+Data+Model">Confluence</a>
 *  Test with Excels: CCA3 target period reporting methodology (worked examples).xlsx, Carbon dioxide example.xlsx
 *  Excel values:
 *  actualEnergyCarbon: Throughput!M29
 *  targetEnergyCarbon: Throughput!M26
 *  energyCarbonDifference: Throughput!M30
 *  weightedConversionFactor: Fuels!B36
 *  targetCo2Emissions: No representation
 *  actualCo2Emissions: Throughput!M36
 *  co2EmissionsDifference: Throughput!M31
 *  actualImprovement: Throughput!M33
 *  targetPeriodResultType: No representation
 *  results.getSurplusGained(): Throughput!M35
 *  buyOutRequired: No representation
 */
@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityCalculationFunctionUtilTest  {

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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(27600000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3400000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3321.2816129).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.425).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(409.1433871).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.0333333333).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(410));
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(27600000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3400000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3321.2816129).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(409.1433871).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.0333333333).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(410));
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(52800000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-21800000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(6353.7561290).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-2623.3311290).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.483333333333333).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(2623));
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_ENERGY_KWH_Fixed_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(27000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(4000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3249.0798387).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.425).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(481.3451613).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.033333333333333).setScale(9, RoundingMode.HALF_UP));

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
        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(19923750).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(11076250).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2397.5501643).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1332.8748357).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.431457431).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(1333));
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(28980000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2020000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3487.3456935).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(243.0793065).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.015873016).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(244));
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(19057500).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(11942500).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2293.3088528).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1437.1161472).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.431457431).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(1438));
    }

    @Test
    void test_ENERGY_KWH_Variable_TOTAL_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(19490625).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(11509375).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2345.4295086).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1384.9954914).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.431457431).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(1385));
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30211968.0851064).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(788031.9148936).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3635.5961627).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(94.8288373).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.055687768).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(95));
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(43944680.8510638).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-12944680.8510638).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(5288.1398730).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-1557.7148730).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.350785340).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(1557));
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(28898404.2553191).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2101595.74468085).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3477.5267643).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(252.8982357).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.055687768).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(253));
    }

    @Test
    void test_ENERGY_KWH_fixed_Variable_PRODUCTS_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.1))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(5000))
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
                                .baselineYear(Year.of(2022))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(29549812.5).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1450187.5).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3555.9148160).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(174.5101840).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.055831573).setScale(9, RoundingMode.HALF_UP));
    }
    
    @Test
    void test_ENERGY_KWH_fixed_Variable_PRODUCTS_diff_base_years_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.1))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(5000))
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
                                .baselineYear(Year.of(2023))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30064788.2110291).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(935211.7889709).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3617.8850826).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(112.5399174).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.055831573).setScale(9, RoundingMode.HALF_UP));
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(22482500).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(8517500).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2705.4606472).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1024.9643528).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.268542199488491).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(1025));
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(28520000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2480000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
        		.isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3431.9910000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(298.4340000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(299));
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30305000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(695000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
				.isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3646.7912782).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(83.6337218).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.099818512).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(84));
    }

    @Test
    void test_ENERGY_KWH_fixed_TOTAL_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(21993750).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(9006250).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
				.isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2646.6462853).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1083.7787147).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.268542199).setScale(9, RoundingMode.HALF_UP));
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30715093.0851064).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(284906.914893616).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
				.isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3696.1403588).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(34.2846412).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.071161049).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(35));
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
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(40494680.8510638).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-9494680.85106383).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
				.isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(4872.9796714).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-1142.5546714).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.295454545).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(1142));
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(0));
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(38179654.2553191).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-7179654.2553191).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
				.isEqualTo(BigDecimal.valueOf(0.1203363).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(4594.3979589).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-863.9729589).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.285302594).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(863));
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(0));
    }


    @Test
    void test_ENERGY_KWH_fixed_PRODUCTS_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30047373.6702128).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(952626.3297872).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1154753).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3469.7301673).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3579.735).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(110.0048327).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.071161049).setScale(9, RoundingMode.HALF_UP));
    }
    
    @Test
    void test_ENERGY_MWh_Fixed_no_SRM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_MWH)
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
                                        .conversionFactor(BigDecimal.valueOf(100.46000))
                                        .build()     
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(27600000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3400000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(120.3362903).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3321281.6129032).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730425).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(409143.3870968).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.0333333333).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(409144));
    }
    
    @Test
    void test_ENERGY_MWH_Variable_TOTAL() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_MWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
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
                                        .conversionFactor(BigDecimal.valueOf(100.46000))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(29123750.0000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1876250).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(120.3362903).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3504644.0352823).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730425).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(225780.9647177).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.020730503).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(225781));
    }
    
    @Test
    void test_ENERGY_MWH_Variable_TOTAL_multiple_custom_fuels() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_MWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
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
                                        .conversionFactor(BigDecimal.valueOf(100.46000))
                                        .build(),
                                 PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel2")
                                        .deliveredEnergy(BigDecimal.valueOf(4000))
                                        .conversionFactor(BigDecimal.valueOf(100.46000))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31004000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(29123750).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1880250).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(120.3337260).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3504569.3517433).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730826.840000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(226257.4882567).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.020604146).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(226258));
    }
    
    @Test
    void test_ENERGY_MWH_TP8_fixed_variable_by_product_diff_base_year() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
        		.targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2028))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.12))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_MWH)
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
                                        .conversionFactor(BigDecimal.valueOf(100.46000))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(38179654.2553191).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-7179654.2553191).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(120.3362903).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(4594397.9588838).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3730425).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-863972.9588838).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.285302594).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(863972));
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void test_ENERGY_MWH_TP8_fixed_variable_by_product_all_base_year_2022() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
        		.targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2028))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.12))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_MWH)
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
                                .baselineYear(Year.of(2022))
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
                                        .conversionFactor(BigDecimal.valueOf(100.46000))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(38170000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-7170000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(120.3362903).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(4593236.2016129).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3730425).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-862811.2016129).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.285302594).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(862811));
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void test_ENERGY_MWH_fixed_Variable_PRODUCTS_diff_base_years_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.1))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_MWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(5000))
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
                                .baselineYear(Year.of(2023))
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
                                        .conversionFactor(BigDecimal.valueOf(100.46000))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30064788.2110291).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(935211.7889709).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(120.3362903).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3617885.0826493).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3730425.0000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(112539.9173507).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.055831573).setScale(9, RoundingMode.HALF_UP));
    }
    
    @Test
    void test_ENERGY_MWH_fixed_Variable_PRODUCTS_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.1))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_MWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(5000))
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
                                .baselineYear(Year.of(2022))
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
                                        .conversionFactor(BigDecimal.valueOf(100.46000))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(29549812.5).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1450187.5000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(120.3362903).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3555914.8159778).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3730425.0000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(174510.1840222).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.055831573).setScale(9, RoundingMode.HALF_UP));
    }
    
    @Test
    void test_ENERGY_KWH_TP7_fixed_variable_by_product_same_base_year() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
        		.targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.488372093))
                .baselineDate(LocalDate.of(2023, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(48.8372093),
                        TargetImprovementType.TP8, BigDecimal.valueOf(61.628),
                        TargetImprovementType.TP9, BigDecimal.valueOf(84.884)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(350100))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2023))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(100000))
                                .throughput(BigDecimal.valueOf(200))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2023))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(800000))
                                .throughput(BigDecimal.valueOf(400))
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
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(7000)).build()           
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(300))
                        .build())
		                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
		                        .variableEnergyConsumptionDataByProduct(List.of(
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Low EI product").actualThroughput(BigDecimal.valueOf(4600)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Hi EI product").actualThroughput(BigDecimal.valueOf(3000)).build()
		                        ))
		                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(17500).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(4322059.1039968).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-4304559.1039968).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.0602760).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(260.5164346).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(1.0548300).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-259.4616046).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.997928421).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(259));
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_ENERGY_GJ_Fixed_no_SRM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
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
                                        .conversionFactor(BigDecimal.valueOf(27.90556))
                                        .build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(27600000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3400000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(33.4267475).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(922578.2317419).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1036229.1733333).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(113650.9415914).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.0333333333).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(113651));
    }
    
    @Test
    void test_ENERGY_GJ_Variable_TOTAL() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10000000))
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
                                        .conversionFactor(BigDecimal.valueOf(27.90556))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(29123750.0000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1876250).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(33.4267475).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(973512.2382860).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(1036229.1733333).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(62716.9350473).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.020730503).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(62717));
    }

    @Test
    void test_ENERGY_GJ_fixed_variable_by_product_2() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.87))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(87),
                        TargetImprovementType.TP8, BigDecimal.valueOf(67),
                        TargetImprovementType.TP9, BigDecimal.valueOf(89)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(1240.234))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(100000))
                                .throughput(BigDecimal.valueOf(2.4))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2023))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(30000))
                                .throughput(BigDecimal.valueOf(4.9))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("New product")
                                .baselineYear(Year.of(2024))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(17000))
                                .throughput(BigDecimal.valueOf(2.1))
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
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(3000)).build()
                        ))
                        .nonStandardFuels(List.of())
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(200))
                        .build())
		                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
		                        .variableEnergyConsumptionDataByProduct(List.of(
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Low EI product").actualThroughput(BigDecimal.valueOf(1.2)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Hi EI product").actualThroughput(BigDecimal.valueOf(2.4)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("New product").actualThroughput(BigDecimal.valueOf(3.2)).build()
		                        ))
		                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(13500).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(14699.3212208).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-1199.3212208).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(21.7043210).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(319.0387861).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(293.0083333).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-26.0304527).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.84937936019).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(26));
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void test_ENERGY_GJ_fixed_variable_by_product_3() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.87))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(87),
                        TargetImprovementType.TP8, BigDecimal.valueOf(67),
                        TargetImprovementType.TP9, BigDecimal.valueOf(89)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(0))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(100000))
                                .throughput(BigDecimal.valueOf(2.4))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2023))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(30000))
                                .throughput(BigDecimal.valueOf(4.9))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("New product")
                                .baselineYear(Year.of(2024))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(17000))
                                .throughput(BigDecimal.valueOf(2.1))
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
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(3000)).build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(300))
                                        .conversionFactor(BigDecimal.valueOf(34.2653700))
                                        .build()
                        ))
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(1000))
                        .build())
		                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
		                        .variableEnergyConsumptionDataByProduct(List.of(
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Low EI product").actualThroughput(BigDecimal.valueOf(100)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Hi EI product").actualThroughput(BigDecimal.valueOf(200)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("New product").actualThroughput(BigDecimal.valueOf(300)).build()
		                        ))
		                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(7400).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(977911.9707907).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-970511.9707907).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(29.8645871).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(29204.9372005).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(220.9979443).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-28983.9392562).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.998738234).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(28983));
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void test_ENERGY_GJ_fixed_variable_by_product() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
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
                                        .conversionFactor(BigDecimal.valueOf(27.90556))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30715093.0851064).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(284906.9148936).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(33.4267475).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(1026705.6618205).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(1036229.1733333).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(9523.5115128).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.071161049).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(9524));
    }
    
    @Test
    void test_ENERGY_GJ_TP8_fixed_variable_by_product() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2028))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.67))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(87),
                        TargetImprovementType.TP8, BigDecimal.valueOf(67),
                        TargetImprovementType.TP9, BigDecimal.valueOf(89)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(1240.234))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(100000))
                                .throughput(BigDecimal.valueOf(2.4))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2023))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(30000))
                                .throughput(BigDecimal.valueOf(4.9))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("New product")
                                .baselineYear(Year.of(2024))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(17000))
                                .throughput(BigDecimal.valueOf(2.1))
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
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(3000)).build()
                        ))
                        .nonStandardFuels(List.of())
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(200))
                        .build())
		                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
		                        .variableEnergyConsumptionDataByProduct(List.of(
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Low EI product").actualThroughput(BigDecimal.valueOf(1.2)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Hi EI product").actualThroughput(BigDecimal.valueOf(2.4)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("New product").actualThroughput(BigDecimal.valueOf(3.2)).build()
		                        ))
		                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(13500).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(37722.9387804).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-24222.9387804).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(21.7043210).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(818.7507719).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(293.0083333).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-525.7424386).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.8514351111).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(525));
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void test_ENERGY_GJ_TP8_fixed_variable_by_product_2() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.055))
                .baselineDate(LocalDate.of(2023, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(5),
                        TargetImprovementType.TP8, BigDecimal.valueOf(6),
                        TargetImprovementType.TP9, BigDecimal.valueOf(10)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(5000000))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2023))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(1000000))
                                .throughput(BigDecimal.valueOf(10000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2024))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(2000000))
                                .throughput(BigDecimal.valueOf(20000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("New product")
                                .baselineYear(Year.of(2025))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(3000000))
                                .throughput(BigDecimal.valueOf(30000))
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
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(3500000.12345678)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(700000.123456789)).build()
                        ))
                        .nonStandardFuels(List.of())
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(1000000))
                        .build())
		                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
		                        .variableEnergyConsumptionDataByProduct(List.of(
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Low EI product").actualThroughput(BigDecimal.valueOf(15000)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Hi EI product").actualThroughput(BigDecimal.valueOf(15000)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("New product").actualThroughput(BigDecimal.valueOf(15000)).build()
		                        ))
		                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(8050000.3827160).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(8089203.7084277).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-39203.3257116).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(29.8881645).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(241771.4511650).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(240599.7357169).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-1171.7154481).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.052941135).setScale(9, RoundingMode.HALF_UP));
    }
    
    @Test
    void test_ENERGY_GJ_TP8_fixed_variable_by_product_3() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.0558526345))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(false)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(4.9849844),
                        TargetImprovementType.TP8, BigDecimal.valueOf(6.1855425),
                        TargetImprovementType.TP9, BigDecimal.valueOf(9.8557805)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(1920258))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(1181778))
                .totalThroughput(BigDecimal.valueOf(5363))
                .throughputUnit("unit")
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(10200300)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(456000.12345)).build()
                        ))
                        .nonStandardFuels(List.of())
                        .build())
		                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
		                        .actualThroughput(BigDecimal.valueOf(10000))
		                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(21876630.12345).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3893507.3434638).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(17983122.7799862).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.1021709).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(397.8031074).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(2235.1547523).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(1837.3516450).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-4.304924551).setScale(9, RoundingMode.HALF_UP));
    }

    @Test
    void test_ENERGY_GJ_fixed_variable_by_product_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.043037975))
                .baselineDate(LocalDate.of(2023, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
                .usedReportingMechanism(false)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(3.7974684),
                        TargetImprovementType.TP8, BigDecimal.valueOf(4.8101266),
                        TargetImprovementType.TP9, BigDecimal.valueOf(8.8607595)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(1000000))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Low EI product")
                                .baselineYear(Year.of(2022))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(1000000))
                                .throughput(BigDecimal.valueOf(10000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Hi EI product")
                                .baselineYear(Year.of(2023))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(1000000))
                                .throughput(BigDecimal.valueOf(10000))
                                .throughputUnit("units")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("New product")
                                .baselineYear(Year.of(2024))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(4000000))
                                .throughput(BigDecimal.valueOf(20000))
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
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5000000)).build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(1000000))
                                        .conversionFactor(BigDecimal.valueOf(0.1))
                                        .build()
                        ))
                        .build())
		                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
		                        .variableEnergyConsumptionDataByProduct(List.of(
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Low EI product").actualThroughput(BigDecimal.valueOf(0)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("Hi EI product").actualThroughput(BigDecimal.valueOf(24000)).build(),
		                                PerformanceDataFacilityProductVariableEnergyData.builder().productName("New product").actualThroughput(BigDecimal.valueOf(20000)).build()
		                        ))
		                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(10100000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(7130593.9612375).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2969406.0387625).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(30.9138064).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(220433.8010966).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(312229.4444444).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(91795.6433478).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-0.364864865).setScale(9, RoundingMode.HALF_UP));
    }
    
    @Test
    void test_ENERGY_GJ_fixed_Variable_PRODUCTS_diff_base_years_TP8_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.1))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(5000))
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
                                .baselineYear(Year.of(2023))
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
                                        .conversionFactor(BigDecimal.valueOf(27.90556))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(30064788.2110291).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(935211.7889709).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(33.4267475).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(1004968.0849792).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(1036229.1733333).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(31261.0883541).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.055831573).setScale(9, RoundingMode.HALF_UP));
    }
    
    @Test
    void test_ENERGY_GJ_fixed_Variable_PRODUCTS_diff_base_years_TP9() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP9)
                .targetYear(Year.of(2030))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.16))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(5000))
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
                                .baselineYear(Year.of(2023))
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
                                        .conversionFactor(BigDecimal.valueOf(27.90556))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(28064668.996960500).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2935331.0030395).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(33.4267475).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(938110.6049869).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(1036229.1733333).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(98118.5683464).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.055975334).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(98119));
    }
    
    @Test
    void test_ENERGY_GJ_fixed_Variable_PRODUCTS_diff_base_years_TP9_INTERIM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP9)
                .targetYear(Year.of(2029))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.14))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_GJ)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(5000))
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
                                .baselineYear(Year.of(2023))
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
                                        .conversionFactor(BigDecimal.valueOf(27.90556))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(31000000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(28728575.4016500).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(2271424.5983500).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(33.4267475).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(960302.8367579).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(1036229.1733333).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(75926.3365754).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.055831573).setScale(9, RoundingMode.HALF_UP));
    }
    
    
    /*
     * CARBON
     */
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755565.39677).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(802620.689655173).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-47055.2928851725).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1403856).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(802.6206896552).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755.56539677).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-47.0552928851725).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.133936896983004).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(47));
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755565.39677).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(828000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-72434.60323).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1403856).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(828).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755.56539677).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-72.4346032).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.160482892477778).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(72));
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755565.39677).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(855724.137931035).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-100158.741161035).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1403856).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(855.7241379).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(755.56539677).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-100.158741161035).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.223000123889007).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_CARBON_KG_fixed_TOTAL_zeros_with_fuel_non_grid() {
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
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1000000)).build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(5000))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.ZERO.setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void test_CARBON_KG_fixed_Variable_PRODUCTS_diff_base_years_TP9() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP9)
                .targetYear(Year.of(2030))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.16))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.CARBON_KG)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(5000))
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
                                .baselineYear(Year.of(2023))
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
                                        .conversionFactor(BigDecimal.valueOf(0.1004600))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730425).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(28064668.9969605).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-24334243.9969605).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.1390355).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(28064.6689970).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-24334.2439970).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.886399574).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(24334));
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void test_CARBON_TONNE_fixed_TOTAL_no_SRM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.08))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.CARBON_TONNE)
                .usedReportingMechanism(false)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(1000000))
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
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(18000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2000000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5678000)).build(),
                                PerformanceDataFacilityFixedConversionFactor.PETROL, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(2145000)).build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Other Fuel 01")
                                        .deliveredEnergy(BigDecimal.valueOf(9000000))
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(5000))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(6771.8047700).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1656000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-1649228.1952300).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1345127).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1656000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(6771.8047700).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-1649228.1952300).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.996237886).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(1649228));
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    void test_CARBON_TONNE_variable_only_TOTAL_no_SRM() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .targetYear(Year.of(2026))
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.04))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.CARBON_TONNE)
                .usedReportingMechanism(false)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(4),
                        TargetImprovementType.TP8, BigDecimal.valueOf(7),
                        TargetImprovementType.TP9, BigDecimal.valueOf(10)
                ))
                .totalFixedEnergy(BigDecimal.ZERO)
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(1103.1552191))
                .totalThroughput(BigDecimal.valueOf(59510703))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(1620000.805)).build(),
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder().deliveredEnergy(BigDecimal.valueOf(5032000.124)).build()
                        ))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(64438326))
                        .build())
                .build();

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);

        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1260.3063925).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1146.7190467).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(113.5873458).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(0.1602819).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1146.7190467).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(1260.3063925).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(113.5873458).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-0.055092039).setScale(9, RoundingMode.HALF_UP));
        assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET);
        assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.ZERO);
        assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.valueOf(114));
    }
    
    @Test
    void test_CARBON_TONNE_fixed_Variable_PRODUCTS_diff_base_years_TP9() {
        final PerformanceDataFacilityCalculationParameters calculatedInfoData = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP9)
                .targetYear(Year.of(2030))
                .tpMultiplier(BigDecimal.TWO)
                .targetImprovement(BigDecimal.valueOf(0.16))
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.CARBON_TONNE)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(5000))
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
                                .baselineYear(Year.of(2023))
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
                                        .conversionFactor(BigDecimal.valueOf(0.1004600))
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

        PerformanceDataFacilityCalculatedResults results = calculateResults(calculatedInfoData, data);
        
        // Verify
        assertThat(results.getActualEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getTargetEnergyCarbon().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(28064668.9969605).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getEnergyCarbonDifference().setScale(7, RoundingMode.HALF_UP))
                .isEqualTo(BigDecimal.valueOf(-28060938.5719605).setScale(7, RoundingMode.HALF_UP));
        assertThat(results.getWeightedConversionFactor().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.1390355).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getTargetCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(28064668.9969605).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualCo2Emissions().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(3730.4250000).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getCo2EmissionsDifference().setScale(7, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(-28060938.5719605).setScale(7, RoundingMode.HALF_UP));
		assertThat(results.getActualImprovement().setScale(9, RoundingMode.HALF_UP))
		        .isEqualTo(BigDecimal.valueOf(0.999886400).setScale(9, RoundingMode.HALF_UP));
		assertThat(results.getTargetPeriodResultType()).isEqualTo(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET);
		assertThat(results.getSurplusGained()).isEqualTo(BigDecimal.valueOf(28060938));
		assertThat(results.getBuyOutRequired()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_INTERIM_TARGET() {
        final Map<TargetImprovementType, BigDecimal> improvements = Map.of(
                TargetImprovementType.TP7, BigDecimal.valueOf(4.9849844),
                TargetImprovementType.TP8, BigDecimal.valueOf(6.1855425),
                TargetImprovementType.TP9, BigDecimal.valueOf(9.8557805)
        );
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP8;

        // Invoke
        BigDecimal result = PerformanceDataFacilityCalculationFunctionUtil.INTERIM_TARGET
                .apply(improvements, targetPeriodType);

        // Verify
        assertThat(result).isEqualTo(BigDecimal.valueOf(0.0558526345));
    }

    @Test
    void test_INTERIM_TARGET_no_previous() {
        final Map<TargetImprovementType, BigDecimal> improvements = Map.of(
                TargetImprovementType.TP7, BigDecimal.valueOf(100),
                TargetImprovementType.TP8, BigDecimal.valueOf(12),
                TargetImprovementType.TP9, BigDecimal.valueOf(16)
        );
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

        // Invoke
        BigDecimal result = PerformanceDataFacilityCalculationFunctionUtil.INTERIM_TARGET
                .apply(improvements, targetPeriodType).setScale(7, RoundingMode.HALF_UP);

        // Verify
        assertThat(result).isEqualTo(BigDecimal.valueOf(0.5).setScale(7, RoundingMode.HALF_UP));
    }

	@Test
    void test_getMultiplier_TP7() {
    	List<TargetPeriodDetailsDTO> targetPeriods = getListOfTargetPeriodDetailsDTO();
    	
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final Year targetYear = Year.of(2026);

        // Invoke
        Optional<BigDecimal> result = PerformanceDataFacilityCalculationFunctionUtil.getTpMultiplier(targetPeriods, targetPeriodType, targetYear);

        // Verify
        assertThat(result).contains(BigDecimal.valueOf(1));
    }

	@Test
    void test_getMultiplier_TP8_interim() {
    	List<TargetPeriodDetailsDTO> targetPeriods = getListOfTargetPeriodDetailsDTO();
    	
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP8;
        final Year targetYear = Year.of(2027);

        // Invoke
        Optional<BigDecimal> result = PerformanceDataFacilityCalculationFunctionUtil.getTpMultiplier(targetPeriods, targetPeriodType, targetYear);

        // Verify
        assertThat(result).contains(BigDecimal.valueOf(1));
    }

	@Test
    void test_getMultiplier_TP8() {
    	List<TargetPeriodDetailsDTO> targetPeriods = getListOfTargetPeriodDetailsDTO();
    	
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP8;
        final Year targetYear = Year.of(2028);

        // Invoke
        Optional<BigDecimal> result = PerformanceDataFacilityCalculationFunctionUtil.getTpMultiplier(targetPeriods, targetPeriodType, targetYear);

        // Verify
        assertThat(result).contains(BigDecimal.valueOf(2));
    }

	private List<TargetPeriodDetailsDTO> getListOfTargetPeriodDetailsDTO() {
		return List.of(
				TargetPeriodDetailsDTO.builder().businessId(TargetPeriodType.TP7)
						.targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
								.targetPeriodYears(
										List.of(TargetPeriodYear.builder().targetYear(Year.of(2026)).build()))
								.build())
						.build(),
				TargetPeriodDetailsDTO.builder().businessId(TargetPeriodType.TP8)
						.targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
								.targetPeriodYears(List.of(TargetPeriodYear.builder().targetYear(Year.of(2027)).build(),
										TargetPeriodYear.builder().targetYear(Year.of(2028)).build()))
								.build())
						.build(),
				TargetPeriodDetailsDTO.builder().businessId(TargetPeriodType.TP9)
						.targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
								.targetPeriodYears(List.of(TargetPeriodYear.builder().targetYear(Year.of(2029)).build(),
										TargetPeriodYear.builder().targetYear(Year.of(2030)).build()))
								.build())
						.build());
	}
    
    private PerformanceDataFacilityCalculatedResults calculateResults(
			final PerformanceDataFacilityCalculationParameters calculatedInfoData,
			final PerformanceDataFacilityInputData data) {
		BigDecimal actualEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_ENERGY_CARBON
				.apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
		BigDecimal targetEnergyCarbon = PerformanceDataFacilityCalculationFunctionUtil.TARGET_ENERGY_CARBON
				.apply(calculatedInfoData, data);
		BigDecimal energyCarbonDifference = PerformanceDataFacilityCalculationFunctionUtil.ENERGY_CARBON_DIFFERENCE
				.apply(actualEnergyCarbon, targetEnergyCarbon);
		BigDecimal weightedConversionFactor = PerformanceDataFacilityCalculationFunctionUtil.WEIGHTED_CONVERSION_FACTOR
				.apply(data.getEnergyFuelDetails(), actualEnergyCarbon, calculatedInfoData.getMeasurementType());
		BigDecimal targetCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.TARGET_TCO2_EMISSIONS
				.apply(targetEnergyCarbon, weightedConversionFactor, calculatedInfoData.getMeasurementType());
		BigDecimal actualCo2Emissions = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_TCO2_EMISSIONS
				.apply(data.getEnergyFuelDetails(), calculatedInfoData.getMeasurementType());
		BigDecimal co2EmissionsDifference = PerformanceDataFacilityCalculationFunctionUtil.TCO2_EMISSIONS_DIFFERENCE
				.apply(actualCo2Emissions, targetCo2Emissions);
		BigDecimal actualImprovement = PerformanceDataFacilityCalculationFunctionUtil.ACTUAL_IMPROVEMENT
				.apply(actualEnergyCarbon, calculatedInfoData, data);
		PerformanceDataFacilityTargetPeriodResultType targetPeriodResultType = PerformanceDataFacilityCalculationFunctionUtil.TARGET_PERIOD_RESULT_TYPE
				.apply(energyCarbonDifference);
		BigDecimal surplusGained = PerformanceDataFacilityCalculationFunctionUtil.SURPLUS_GAINED
				.apply(co2EmissionsDifference);
		BigDecimal buyOutRequired = PerformanceDataFacilityCalculationFunctionUtil.BUY_OUT_REQUIRED
				.apply(co2EmissionsDifference);
		return PerformanceDataFacilityCalculatedResults.builder().actualEnergyCarbon(actualEnergyCarbon)
				.targetEnergyCarbon(targetEnergyCarbon).energyCarbonDifference(energyCarbonDifference)
				.weightedConversionFactor(weightedConversionFactor).targetCo2Emissions(targetCo2Emissions)
				.actualCo2Emissions(actualCo2Emissions).co2EmissionsDifference(co2EmissionsDifference)
				.actualImprovement(actualImprovement).targetPeriodResultType(targetPeriodResultType)
				.surplusGained(surplusGained).buyOutRequired(buyOutRequired).build();
	}
}
