package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.transform;

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
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityUploadCsvData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityProcessingInputDataMapperTest {

    @Test
    void toPerformanceDataFacilityInputData_KWH_Fixed() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .actualThroughput(BigDecimal.valueOf(10500))
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
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                        .adjustedThroughput(BigDecimal.valueOf(7000).setScale(7, RoundingMode.HALF_UP))
                        .totalTargetVariableEnergy(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }
    
    @Test
    void toPerformanceDataFacilityInputData_zero_amount_fuels() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .nonGridElectricity(BigDecimal.ZERO)
                .naturalGas(BigDecimal.ZERO)
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(null)
                .otherFuelName2("Biofuel")
                .otherFuelConversionFactor2(BigDecimal.ZERO)
                .otherFuelAmount2(BigDecimal.ZERO)
                .otherFuelName3(null)
                .otherFuelConversionFactor3(null)
                .otherFuelAmount3(BigDecimal.ZERO)
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .actualThroughput(BigDecimal.valueOf(10500))
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
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(null)
                                .primaryEnergy(BigDecimal.ZERO)
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                        .adjustedThroughput(BigDecimal.valueOf(7000).setScale(7, RoundingMode.HALF_UP))
                        .totalTargetVariableEnergy(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_KWH_Fixed_no_csv_srm() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(null)
                .actualThroughput(BigDecimal.valueOf(10500))
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
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(null)
                        .throughputAdjustmentFactor(null)
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                        .adjustedThroughput(null)
                        .totalTargetVariableEnergy(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_KWH_Fixed_no_csv_actualThroughput() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .actualThroughput(null)
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
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(null)
                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                        .adjustedThroughput(null)
                        .totalTargetVariableEnergy(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_no_csv_otherFuelAmount() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .nonGridElectricity(BigDecimal.ZERO)
                .naturalGas(BigDecimal.ZERO)
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(null)
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .actualThroughput(BigDecimal.valueOf(10500))
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
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(null)
                                .primaryEnergy(BigDecimal.ZERO)
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                        .adjustedThroughput(BigDecimal.valueOf(7000).setScale(7, RoundingMode.HALF_UP))
                        .totalTargetVariableEnergy(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_no_csv_otherFuelConversionFactor() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(null)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .actualThroughput(BigDecimal.valueOf(10500))
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
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(null)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.ZERO)
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                        .adjustedThroughput(BigDecimal.valueOf(7000).setScale(7, RoundingMode.HALF_UP))
                        .totalTargetVariableEnergy(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_read_all_fuels() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(1))
                .nonGridElectricity(BigDecimal.valueOf(2))
                .naturalGas(BigDecimal.valueOf(3))
                .lpg(BigDecimal.valueOf(4))
                .gasDieselOil(BigDecimal.valueOf(5))
                .kerosene(BigDecimal.valueOf(6))
                .fuelOil(BigDecimal.valueOf(7))
                .coal(BigDecimal.valueOf(8))
                .coke(BigDecimal.valueOf(9))
                .petrol(BigDecimal.valueOf(10))
                .nitrogen(BigDecimal.valueOf(11))
                .carbonDioxide(BigDecimal.valueOf(12))
                .ethane(BigDecimal.valueOf(13))
                .naphtha(BigDecimal.valueOf(14))
                .petroleumCoke(BigDecimal.valueOf(15))
                .refineryGas(BigDecimal.valueOf(16))
                .otherFuelName1("otherFuelName1")
                .otherFuelConversionFactor1(BigDecimal.valueOf(0.1))
                .otherFuelAmount1(BigDecimal.valueOf(1))
                .otherFuelName2("otherFuelName2")
                .otherFuelConversionFactor2(BigDecimal.valueOf(0.2))
                .otherFuelAmount2(BigDecimal.valueOf(2))
                .otherFuelName3("otherFuelName3")
                .otherFuelConversionFactor3(BigDecimal.valueOf(0.3))
                .otherFuelAmount3(BigDecimal.valueOf(3))
                .otherFuelName4("otherFuelName4")
                .otherFuelConversionFactor4(BigDecimal.valueOf(0.4))
                .otherFuelAmount4(BigDecimal.valueOf(4))
                .otherFuelName5("otherFuelName5")
                .otherFuelConversionFactor5(BigDecimal.valueOf(0.5))
                .otherFuelAmount5(BigDecimal.valueOf(5))
                .otherFuelName6("otherFuelName6")
                .otherFuelConversionFactor6(BigDecimal.valueOf(0.6))
                .otherFuelAmount6(BigDecimal.valueOf(6))
                .otherFuelName7("otherFuelName7")
                .otherFuelConversionFactor7(BigDecimal.valueOf(0.7))
                .otherFuelAmount7(BigDecimal.valueOf(7))
                .otherFuelName8("otherFuelName8")
                .otherFuelConversionFactor8(BigDecimal.valueOf(0.8))
                .otherFuelAmount8(BigDecimal.valueOf(8))
                .otherFuelName9("otherFuelName9")
                .otherFuelConversionFactor9(BigDecimal.valueOf(0.9))
                .otherFuelAmount9(BigDecimal.valueOf(9))
                .otherFuelName10("otherFuelName10")
                .otherFuelConversionFactor10(BigDecimal.valueOf(1))
                .otherFuelAmount10(BigDecimal.valueOf(10))
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
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        Map<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption> standardFuels =
                new EnumMap<>(PerformanceDataFacilityFixedConversionFactor.class);
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(1))
                        .primaryEnergy(BigDecimal.valueOf(2.1).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(2))
                        .primaryEnergy(BigDecimal.valueOf(2).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(3))
                        .primaryEnergy(BigDecimal.valueOf(3).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.LPG,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(4))
                        .primaryEnergy(BigDecimal.valueOf(4).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(5))
                        .primaryEnergy(BigDecimal.valueOf(5).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.KEROSENE,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(6))
                        .primaryEnergy(BigDecimal.valueOf(6).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.FUEL_OIL,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(7))
                        .primaryEnergy(BigDecimal.valueOf(7).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.COAL,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(8))
                        .primaryEnergy(BigDecimal.valueOf(8).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.COKE,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(9))
                        .primaryEnergy(BigDecimal.valueOf(9).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.PETROL,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(10))
                        .primaryEnergy(BigDecimal.valueOf(10).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.NITROGEN_COOLING,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(11))
                        .primaryEnergy(BigDecimal.valueOf(23.1).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.CARBON_DIOXIDE_COOLING,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(12))
                        .primaryEnergy(BigDecimal.valueOf(25.2).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.ETHANE,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(13))
                        .primaryEnergy(BigDecimal.valueOf(13).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.NAPHTHA,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(14))
                        .primaryEnergy(BigDecimal.valueOf(14).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.PETROLEUM_COKE,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(15))
                        .primaryEnergy(BigDecimal.valueOf(15).setScale(7, RoundingMode.HALF_UP))
                        .build());
        standardFuels.put(PerformanceDataFacilityFixedConversionFactor.REFINERY_GAS,
                PerformanceDataFacilityFuelEnergyConsumption.builder()
                        .deliveredEnergy(BigDecimal.valueOf(16))
                        .primaryEnergy(BigDecimal.valueOf(16).setScale(7, RoundingMode.HALF_UP))
                        .build());

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result.getEnergyFuelDetails().getStandardFuels()).containsExactlyInAnyOrderEntriesOf(standardFuels);
        assertThat(result.getEnergyFuelDetails().getNonStandardFuels()).containsExactlyInAnyOrder(
                PerformanceDataFacilityNonStandardFuel.builder().name("otherFuelName1").conversionFactor(BigDecimal.valueOf(0.1))
                        .deliveredEnergy(BigDecimal.valueOf(1)).primaryEnergy(BigDecimal.valueOf(1).setScale(7, RoundingMode.HALF_UP)).build(),
                PerformanceDataFacilityNonStandardFuel.builder().name("otherFuelName2").conversionFactor(BigDecimal.valueOf(0.2))
                        .deliveredEnergy(BigDecimal.valueOf(2)).primaryEnergy(BigDecimal.valueOf(2).setScale(7, RoundingMode.HALF_UP)).build(),
                PerformanceDataFacilityNonStandardFuel.builder().name("otherFuelName3").conversionFactor(BigDecimal.valueOf(0.3))
                        .deliveredEnergy(BigDecimal.valueOf(3)).primaryEnergy(BigDecimal.valueOf(3).setScale(7, RoundingMode.HALF_UP)).build(),
                PerformanceDataFacilityNonStandardFuel.builder().name("otherFuelName4").conversionFactor(BigDecimal.valueOf(0.4))
                        .deliveredEnergy(BigDecimal.valueOf(4)).primaryEnergy(BigDecimal.valueOf(4).setScale(7, RoundingMode.HALF_UP)).build(),
                PerformanceDataFacilityNonStandardFuel.builder().name("otherFuelName5").conversionFactor(BigDecimal.valueOf(0.5))
                        .deliveredEnergy(BigDecimal.valueOf(5)).primaryEnergy(BigDecimal.valueOf(5).setScale(7, RoundingMode.HALF_UP)).build(),
                PerformanceDataFacilityNonStandardFuel.builder().name("otherFuelName6").conversionFactor(BigDecimal.valueOf(0.6))
                        .deliveredEnergy(BigDecimal.valueOf(6)).primaryEnergy(BigDecimal.valueOf(6).setScale(7, RoundingMode.HALF_UP)).build(),
                PerformanceDataFacilityNonStandardFuel.builder().name("otherFuelName7").conversionFactor(BigDecimal.valueOf(0.7))
                        .deliveredEnergy(BigDecimal.valueOf(7)).primaryEnergy(BigDecimal.valueOf(7).setScale(7, RoundingMode.HALF_UP)).build(),
                PerformanceDataFacilityNonStandardFuel.builder().name("otherFuelName8").conversionFactor(BigDecimal.valueOf(0.8))
                        .deliveredEnergy(BigDecimal.valueOf(8)).primaryEnergy(BigDecimal.valueOf(8).setScale(7, RoundingMode.HALF_UP)).build(),
                PerformanceDataFacilityNonStandardFuel.builder().name("otherFuelName9").conversionFactor(BigDecimal.valueOf(0.9))
                        .deliveredEnergy(BigDecimal.valueOf(9)).primaryEnergy(BigDecimal.valueOf(9).setScale(7, RoundingMode.HALF_UP)).build(),
                PerformanceDataFacilityNonStandardFuel.builder().name("otherFuelName10").conversionFactor(BigDecimal.valueOf(1))
                        .deliveredEnergy(BigDecimal.valueOf(10)).primaryEnergy(BigDecimal.valueOf(10).setScale(7, RoundingMode.HALF_UP)).build()
        );
    }

    @Test
    void toPerformanceDataFacilityInputData_KWH_Totals() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .actualThroughput(BigDecimal.valueOf(10500))
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
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(30000000))
                .totalThroughput(BigDecimal.valueOf(10000))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                        .adjustedThroughput(BigDecimal.valueOf(7000).setScale(7, RoundingMode.HALF_UP))
                        .totalTargetVariableEnergy(BigDecimal.valueOf(19320000).setScale(7, RoundingMode.HALF_UP))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_KWH_Totals_no_csv_srm() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(null)
                .actualThroughput(BigDecimal.valueOf(10500))
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
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(30000000))
                .totalThroughput(BigDecimal.valueOf(10000))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(null)
                        .throughputAdjustmentFactor(null)
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                        .adjustedThroughput(null)
                        .totalTargetVariableEnergy(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_KWH_Totals_no_csv_actualThroughput() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .actualThroughput(null)
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
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(30000000))
                .totalThroughput(BigDecimal.valueOf(10000))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(null)
                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                        .adjustedThroughput(null)
                        .totalTargetVariableEnergy(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_KWH_Products() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .productName1("Low EI product")
                .productActualThroughput1(BigDecimal.valueOf(6000))
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

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .totalTargetVariableEnergy(BigDecimal.valueOf(4600000).setScale(7, RoundingMode.HALF_UP))
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("Low EI product")
                                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                                        .actualThroughput(BigDecimal.valueOf(6000))
                                        .adjustedThroughput(BigDecimal.valueOf(4000).setScale(7, RoundingMode.HALF_UP))
                                        .targetEnergy(BigDecimal.valueOf(4600000).setScale(7, RoundingMode.HALF_UP))
                                        .build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("Hi EI product")
                                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                                        .actualThroughput(BigDecimal.ZERO)
                                        .adjustedThroughput(BigDecimal.ZERO)
                                        .targetEnergy(BigDecimal.ZERO)
                                        .build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("New product")
                                        .targetImprovement(BigDecimal.valueOf(0.0212766).setScale(7, RoundingMode.HALF_UP))
                                        .actualThroughput(BigDecimal.ZERO)
                                        .adjustedThroughput(BigDecimal.ZERO)
                                        .targetEnergy(BigDecimal.ZERO)
                                        .build()
                        ))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_KWH_Products_product_not_exist() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .productName1("Low EI product")
                .productActualThroughput1(BigDecimal.valueOf(6000))
                .productName2("New product")
                .productActualThroughput2(BigDecimal.valueOf(100))
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
                                .build()
                ))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .totalTargetVariableEnergy(BigDecimal.valueOf(4600000).setScale(7, RoundingMode.HALF_UP))
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("Low EI product")
                                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                                        .actualThroughput(BigDecimal.valueOf(6000))
                                        .adjustedThroughput(BigDecimal.valueOf(4000).setScale(7, RoundingMode.HALF_UP))
                                        .targetEnergy(BigDecimal.valueOf(4600000).setScale(7, RoundingMode.HALF_UP))
                                        .build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("New product")
                                        .targetImprovement(BigDecimal.ZERO)
                                        .actualThroughput(BigDecimal.valueOf(100))
                                        .adjustedThroughput(BigDecimal.ZERO)
                                        .targetEnergy(BigDecimal.ZERO)
                                        .build()
                        ))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_KWH_Products_no_csv_product_name() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .productName1(null)
                .productActualThroughput1(BigDecimal.valueOf(6000))
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
                                .build()
                ))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .totalTargetVariableEnergy(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP))
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName(null)
                                        .targetImprovement(BigDecimal.ZERO)
                                        .actualThroughput(BigDecimal.valueOf(6000))
                                        .adjustedThroughput(BigDecimal.ZERO)
                                        .targetEnergy(BigDecimal.ZERO)
                                        .build(),
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("Low EI product")
                                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                                        .actualThroughput(BigDecimal.ZERO)
                                        .adjustedThroughput(BigDecimal.ZERO)
                                        .targetEnergy(BigDecimal.ZERO)
                                        .build()
                        ))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_KWH_Products_no_csv_product_actualThroughput() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .productName1("Low EI product")
                .productActualThroughput1(null)
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
                                .build()
                ))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(0.6666667))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .totalTargetVariableEnergy(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP))
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("Low EI product")
                                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                                        .actualThroughput(null)
                                        .adjustedThroughput(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP))
                                        .targetEnergy(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP))
                                        .build()
                        ))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_KWH_Products_no_csv_srm() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(null)
                .productName1("Low EI product")
                .productActualThroughput1(BigDecimal.valueOf(6000))
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
                                .build()
                ))
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(10000000))
                                        .primaryEnergy(BigDecimal.valueOf(21000000).setScale(7, RoundingMode.HALF_UP))
                                        .build()))
                        .nonStandardFuels(List.of(PerformanceDataFacilityNonStandardFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.ZERO)
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryEnergy(BigDecimal.valueOf(1500000).setScale(7, RoundingMode.HALF_UP))
                                .build()))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(null)
                        .throughputAdjustmentFactor(null)
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .totalTargetVariableEnergy(BigDecimal.ZERO)
                        .variableEnergyConsumptionDataByProduct(List.of(
                                PerformanceDataFacilityProductVariableEnergyData.builder()
                                        .productName("Low EI product")
                                        .targetImprovement(BigDecimal.valueOf(0.08).setScale(7, RoundingMode.HALF_UP))
                                        .actualThroughput(BigDecimal.valueOf(6000))
                                        .adjustedThroughput(BigDecimal.ZERO)
                                        .targetEnergy(BigDecimal.ZERO)
                                        .build()
                        ))
                        .build())
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityInputData_read_all_Products() {
        final PerformanceDataFacilityUploadCsvData csvData = PerformanceDataFacilityUploadCsvData.builder()
                .gridElectricity(BigDecimal.valueOf(10000000))
                .otherFuelName1("Biofuel")
                .otherFuelConversionFactor1(BigDecimal.ZERO)
                .otherFuelAmount1(BigDecimal.valueOf(1500000))
                .atLeastSeventyPercentEnergyUsed(true)
                .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                .productName1("productName1")
                .productActualThroughput1(BigDecimal.valueOf(1))
                .productName2("productName2")
                .productActualThroughput2(BigDecimal.valueOf(2))
                .productName3("productName3")
                .productActualThroughput3(BigDecimal.valueOf(3))
                .productName4("productName4")
                .productActualThroughput4(BigDecimal.valueOf(4))
                .productName5("productName5")
                .productActualThroughput5(BigDecimal.valueOf(5))
                .productName6("productName6")
                .productActualThroughput6(BigDecimal.valueOf(6))
                .productName7("productName7")
                .productActualThroughput7(BigDecimal.valueOf(7))
                .productName8("productName8")
                .productActualThroughput8(BigDecimal.valueOf(8))
                .productName9("productName9")
                .productActualThroughput9(BigDecimal.valueOf(9))
                .productName10("productName10")
                .productActualThroughput10(BigDecimal.valueOf(10))
                .productName11("productName11")
                .productActualThroughput11(BigDecimal.valueOf(11))
                .productName12("productName12")
                .productActualThroughput12(BigDecimal.valueOf(12))
                .productName13("productName13")
                .productActualThroughput13(BigDecimal.valueOf(13))
                .productName14("productName14")
                .productActualThroughput14(BigDecimal.valueOf(14))
                .productName15("productName15")
                .productActualThroughput15(BigDecimal.valueOf(15))
                .productName16("productName16")
                .productActualThroughput16(BigDecimal.valueOf(16))
                .productName17("productName17")
                .productActualThroughput17(BigDecimal.valueOf(17))
                .productName18("productName18")
                .productActualThroughput18(BigDecimal.valueOf(18))
                .productName19("productName19")
                .productActualThroughput19(BigDecimal.valueOf(19))
                .productName20("productName20")
                .productActualThroughput20(BigDecimal.valueOf(20))
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
                .variableEnergyConsumptionDataByProduct(List.of())
                .lastYearPerTp(Map.of(
                        TargetPeriodType.TP7, 2026,
                        TargetPeriodType.TP8, 2028,
                        TargetPeriodType.TP9, 2030
                ))
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = PerformanceDataFacilityProcessingInputDataMapper
                .toPerformanceDataFacilityInputData(csvData, calculationParameters);

        // Verify
        assertThat(result.getThroughputDetails().getVariableEnergyConsumptionDataByProduct()).containsExactlyInAnyOrder(
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName1")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(1))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName2")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(2))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName3")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(3))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName4")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(4))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName5")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(5))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName6")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(6))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName7")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(7))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName8")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(8))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName9")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(9))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName10")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(10))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName11")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(11))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName12")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(12))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName13")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(13))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName14")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(14))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName15")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(15))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName16")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(16))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName17")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(17))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName18")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(18))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName19")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(19))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build(),
                PerformanceDataFacilityProductVariableEnergyData.builder()
                        .productName("productName20")
                        .targetImprovement(BigDecimal.ZERO)
                        .actualThroughput(BigDecimal.valueOf(20))
                        .adjustedThroughput(BigDecimal.ZERO)
                        .targetEnergy(BigDecimal.ZERO)
                        .build()
        );
    }
}
