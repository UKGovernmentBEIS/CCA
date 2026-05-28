package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEnergyFuelDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityProductVariableEnergyData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityReferenceData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormSubmitMapperTest {

    private final PerformanceDataFacilityDigitalFormSubmitMapper mapper = Mappers
            .getMapper(PerformanceDataFacilityDigitalFormSubmitMapper.class);

    @Test
    void toPerformanceDataFacilityCalculationParameters() {
        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(TargetPeriodYear.builder().targetYear(Year.of(2026)).build()))
                                .build())
                        .build()
        );
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP8)
                        .reportType(PerformanceDataReportType.FINAL)
                        .targetPeriodYear(Year.of(2026))
                        .referenceData(PerformanceDataFacilityReferenceData.builder()
                                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                                        .baselineDate(LocalDate.of(2018, 1, 1))
                                        .isTwelveMonths(true)
                                        .energyCarbonFactor(BigDecimal.valueOf(0.01))
                                        .measurementType(MeasurementType.ENERGY_KWH)
                                        .usedReportingMechanism(true)
                                        .improvements(Map.of(TargetImprovementType.TP8, BigDecimal.ONE))
                                        .totalFixedEnergy(BigDecimal.TEN)
                                        .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                                        .baselineVariableEnergy(BigDecimal.TWO)
                                        .totalThroughput(BigDecimal.ONE)
                                        .throughputUnit("unit")
                                        .baselineEnergyCarbonIntensity(BigDecimal.TWO)
                                        .variableEnergyConsumptionDataByProduct(List.of(
                                                ProductVariableEnergyConsumptionData.builder()
                                                        .productName("name")
                                                        .baselineYear(Year.of(2026))
                                                        .build()
                                        ))
                                        .build())
                                .tpMultiplier(BigDecimal.TWO)
                                .build())
                        .build();

        final PerformanceDataFacilityCalculationParameters expected =
                PerformanceDataFacilityCalculationParameters.builder()
                        .baselineDate(LocalDate.of(2018, 1, 1))
                        .isTwelveMonths(true)
                        .energyCarbonFactor(BigDecimal.valueOf(0.01))
                        .measurementType(MeasurementType.ENERGY_KWH)
                        .usedReportingMechanism(true)
                        .improvements(Map.of(TargetImprovementType.TP8, BigDecimal.ONE))
                        .totalFixedEnergy(BigDecimal.TEN)
                        .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                        .baselineVariableEnergy(BigDecimal.TWO)
                        .totalThroughput(BigDecimal.ONE)
                        .throughputUnit("unit")
                        .baselineEnergyCarbonIntensity(BigDecimal.TWO)
                        .variableEnergyConsumptionDataByProduct(List.of(
                                ProductVariableEnergyConsumptionData.builder()
                                        .productName("name")
                                        .baselineYear(Year.of(2026))
                                        .build()
                        ))
                        .targetPeriodType(TargetPeriodType.TP8)
                        .targetYear(Year.of(2026))
                        .reportType(PerformanceDataReportType.FINAL)
                        .tpMultiplier(BigDecimal.TWO)
                        .targetImprovement(BigDecimal.valueOf(0.01))
                        .lastYearPerTp(Map.of(TargetPeriodType.TP7, 2026))
                        .build();
        // Invoke
        PerformanceDataFacilityCalculationParameters result = mapper.toPerformanceDataFacilityCalculationParameters(taskPayload, targetPeriods);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityCalculationParameters_with_INTERIM() {
        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(TargetPeriodYear.builder().targetYear(Year.of(2026)).build()))
                                .build())
                        .build()
        );
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP8)
                        .reportType(PerformanceDataReportType.INTERIM)
                        .targetPeriodYear(Year.of(2026))
                        .referenceData(PerformanceDataFacilityReferenceData.builder()
                                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                                        .baselineDate(LocalDate.of(2018, 1, 1))
                                        .isTwelveMonths(true)
                                        .energyCarbonFactor(BigDecimal.valueOf(0.01))
                                        .measurementType(MeasurementType.ENERGY_KWH)
                                        .usedReportingMechanism(true)
                                        .improvements(Map.of(TargetImprovementType.TP8, BigDecimal.ONE))
                                        .totalFixedEnergy(BigDecimal.TEN)
                                        .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                                        .baselineVariableEnergy(BigDecimal.TWO)
                                        .totalThroughput(BigDecimal.ONE)
                                        .throughputUnit("unit")
                                        .baselineEnergyCarbonIntensity(BigDecimal.TWO)
                                        .variableEnergyConsumptionDataByProduct(List.of(
                                                ProductVariableEnergyConsumptionData.builder()
                                                        .productName("name")
                                                        .baselineYear(Year.of(2026))
                                                        .build()
                                        ))
                                        .build())
                                .tpMultiplier(BigDecimal.TWO)
                                .build())
                        .build();

        final PerformanceDataFacilityCalculationParameters expected =
                PerformanceDataFacilityCalculationParameters.builder()
                        .baselineDate(LocalDate.of(2018, 1, 1))
                        .isTwelveMonths(true)
                        .energyCarbonFactor(BigDecimal.valueOf(0.01))
                        .measurementType(MeasurementType.ENERGY_KWH)
                        .usedReportingMechanism(true)
                        .improvements(Map.of(TargetImprovementType.TP8, BigDecimal.ONE))
                        .totalFixedEnergy(BigDecimal.TEN)
                        .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                        .baselineVariableEnergy(BigDecimal.TWO)
                        .totalThroughput(BigDecimal.ONE)
                        .throughputUnit("unit")
                        .baselineEnergyCarbonIntensity(BigDecimal.TWO)
                        .variableEnergyConsumptionDataByProduct(List.of(
                                ProductVariableEnergyConsumptionData.builder()
                                        .productName("name")
                                        .baselineYear(Year.of(2026))
                                        .build()
                        ))
                        .targetPeriodType(TargetPeriodType.TP8)
                        .targetYear(Year.of(2026))
                        .reportType(PerformanceDataReportType.INTERIM)
                        .tpMultiplier(BigDecimal.TWO)
                        .targetImprovement(BigDecimal.valueOf(0.5).setScale(7, RoundingMode.HALF_UP))
                        .lastYearPerTp(Map.of(TargetPeriodType.TP7, 2026))
                        .build();
        // Invoke
        PerformanceDataFacilityCalculationParameters result = mapper.toPerformanceDataFacilityCalculationParameters(taskPayload, targetPeriods);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacility() {
        final PerformanceDataFacilityThroughputDetails throughputDetails = PerformanceDataFacilityThroughputDetails.builder()
                .actualThroughput(BigDecimal.valueOf(100))
                .targetImprovement(BigDecimal.valueOf(200))
                .adjustedThroughput(BigDecimal.valueOf(300))
                .totalTargetVariableEnergy(BigDecimal.valueOf(400))
                .variableEnergyConsumptionDataByProduct(List.of(
                        PerformanceDataFacilityProductVariableEnergyData.builder()
                                .productName("productName")
                                .targetImprovement(BigDecimal.valueOf(100))
                                .actualThroughput(BigDecimal.valueOf(200))
                                .adjustedThroughput(BigDecimal.valueOf(300))
                                .targetEnergy(BigDecimal.valueOf(400))
                                .build()
                ))
                .build();
        final PerformanceDataFacilityContainer container = PerformanceDataFacilityContainer.builder()
                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                        .measurementType(MeasurementType.ENERGY_KWH)
                        .build())
                .energyFuelDetails(PerformanceDataFacilityEnergyFuelDetails.builder()
                        .fuels(List.of(
                                PerformanceDataFacilityFuel.builder()
                                        .name("energy")
                                        .fixedConversionFactorCode(PerformanceDataFacilityFixedConversionFactor.LPG)
                                        .deliveredEnergy(BigDecimal.ONE)
                                        .primaryEnergy(BigDecimal.TWO)
                                        .build(),
                                PerformanceDataFacilityFuel.builder()
                                        .name("biofuel")
                                        .conversionFactor(BigDecimal.valueOf(11))
                                        .deliveredEnergy(BigDecimal.valueOf(12))
                                        .primaryEnergy(BigDecimal.valueOf(13))
                                        .build()
                        ))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.TEN)
                        .throughputAdjustmentFactor(BigDecimal.ZERO)
                        .build())
                .throughputDetails(throughputDetails)
                .calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
                        .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .build())
                .build();

        final PerformanceDataFacilityInputData expected = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.LPG,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.ONE)
                                        .primaryEnergy(BigDecimal.TWO)
                                        .build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("biofuel")
                                        .conversionFactor(BigDecimal.valueOf(11))
                                        .deliveredEnergy(BigDecimal.valueOf(12))
                                        .primaryEnergy(BigDecimal.valueOf(13))
                                        .build()
                        ))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.TEN)
                        .throughputAdjustmentFactor(BigDecimal.ZERO)
                        .build())
                .throughputDetails(throughputDetails)
                .build();

        // Invoke
        PerformanceDataFacilityInputData result = mapper.toPerformanceDataFacilityInputData(container);

        // Verify
        assertThat(result).isEqualTo(expected);
    }
}
