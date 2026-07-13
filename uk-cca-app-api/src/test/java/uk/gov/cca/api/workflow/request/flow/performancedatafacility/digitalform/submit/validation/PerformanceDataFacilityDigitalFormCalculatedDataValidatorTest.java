package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormCalculatedDataValidatorTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormCalculatedDataValidator validator;

    @Test
    void validateCalculatedData_FINAL_valid() {
        final PerformanceDataFacilityCalculationParameters parameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2028))
                .reportType(PerformanceDataReportType.FINAL)
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
                .build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
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
                .calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
                        .actualEnergyCarbon(BigDecimal.valueOf(755565.39677))
                        .targetEnergyCarbon(BigDecimal.valueOf(855724.1379310))
                        .energyCarbonDifference(BigDecimal.valueOf(-100158.7411610))
                        .targetImprovement(BigDecimal.valueOf(0.12))
                        .weightedConversionFactor(BigDecimal.valueOf(0.1403856))
                        .targetCo2Emissions(BigDecimal.valueOf(855.7241379))
                        .actualCo2Emissions(BigDecimal.valueOf(755.5653968))
                        .co2EmissionsDifference(BigDecimal.valueOf(-100.1587412))
                        .actualImprovement(BigDecimal.valueOf(0.2230001))
                        .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .surplusGained(BigDecimal.valueOf(100))
                        .buyOutRequired(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        List<BusinessValidationResult> results = validator.validateCalculatedData(performanceData, parameters);

        // Verify
        assertThat(results.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
    }

    @Test
    void validateCalculatedData_INTERIM_valid() {
        final PerformanceDataFacilityCalculationParameters parameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .reportType(PerformanceDataReportType.INTERIM)
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
                .build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
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
                .calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
                        .actualEnergyCarbon(BigDecimal.valueOf(755565.39677))
                        .targetEnergyCarbon(BigDecimal.valueOf(875172.4137931))
                        .energyCarbonDifference(BigDecimal.valueOf(-119607.0170231))
                        .targetImprovement(BigDecimal.valueOf(0.1))
                        .weightedConversionFactor(BigDecimal.valueOf(0.1403856))
                        .targetCo2Emissions(BigDecimal.valueOf(875.1724138))
                        .actualCo2Emissions(BigDecimal.valueOf(755.5653968))
                        .co2EmissionsDifference(BigDecimal.valueOf(-119.6070170))
                        .actualImprovement(BigDecimal.valueOf(0.2230001))
                        .build())
                .build();

        // Invoke
        List<BusinessValidationResult> results = validator.validateCalculatedData(performanceData, parameters);

        // Verify
        assertThat(results.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
    }

    @Test
    void validateCalculatedData_FINAL_not_valid() {
        final PerformanceDataFacilityCalculationParameters parameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .reportType(PerformanceDataReportType.FINAL)
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
                .build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
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
                .calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
                        .actualEnergyCarbon(BigDecimal.valueOf(755565.39677))
                        .targetEnergyCarbon(BigDecimal.valueOf(855724.1379310))
                        .energyCarbonDifference(BigDecimal.valueOf(-100158.7411610))
                        .targetImprovement(BigDecimal.valueOf(0.12))
                        .weightedConversionFactor(BigDecimal.valueOf(0.1403856))
                        .targetCo2Emissions(BigDecimal.valueOf(855.7241379))
                        .actualCo2Emissions(BigDecimal.valueOf(755.5653968))
                        .co2EmissionsDifference(BigDecimal.valueOf(-100.1587412))
                        .actualImprovement(BigDecimal.valueOf(0.2230001))
                        .build())
                .build();

        // Invoke
        List<BusinessValidationResult> results = validator.validateCalculatedData(performanceData, parameters);

        // Verify
        assertThat(results.stream().filter(r -> !r.isValid()).count()).isEqualTo(3);
    }

    @Test
    void validateCalculatedData_INTERIM_not_valid() {
        final PerformanceDataFacilityCalculationParameters parameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .targetYear(Year.of(2027))
                .reportType(PerformanceDataReportType.INTERIM)
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
                .build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
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
                .calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
                        .actualEnergyCarbon(BigDecimal.valueOf(755565.39677))
                        .targetEnergyCarbon(BigDecimal.valueOf(875172.4137931))
                        .energyCarbonDifference(BigDecimal.valueOf(-119607.0170231))
                        .targetImprovement(BigDecimal.valueOf(0.1))
                        .weightedConversionFactor(BigDecimal.valueOf(0.1403856))
                        .targetCo2Emissions(BigDecimal.valueOf(875.1724138))
                        .actualCo2Emissions(BigDecimal.valueOf(755.5653968))
                        .co2EmissionsDifference(BigDecimal.valueOf(-119.6070170))
                        .actualImprovement(BigDecimal.valueOf(0.2230001))
                        .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .surplusGained(BigDecimal.valueOf(119))
                        .buyOutRequired(BigDecimal.ZERO)
                        .build())
                .build();

        // Invoke
        List<BusinessValidationResult> results = validator.validateCalculatedData(performanceData, parameters);

        // Verify
        assertThat(results.stream().filter(r -> !r.isValid()).count()).isEqualTo(3);
    }

    @Test
    void validateCalculatedData_empty_not_valid() {
        final PerformanceDataFacilityCalculationParameters parameters = PerformanceDataFacilityCalculationParameters.builder().build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder().build();

        // Invoke
        List<BusinessValidationResult> results = validator.validateCalculatedData(performanceData, parameters);

        // Verify
        assertThat(results.stream().filter(r -> !r.isValid()).count()).isEqualTo(1);
    }
}
