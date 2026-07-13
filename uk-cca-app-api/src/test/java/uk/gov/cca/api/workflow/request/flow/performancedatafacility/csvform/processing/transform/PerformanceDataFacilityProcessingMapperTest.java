package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityProcessingMapperTest {

    private final PerformanceDataFacilityProcessingMapper mapper = Mappers
            .getMapper(PerformanceDataFacilityProcessingMapper.class);

    @Test
    void toPerformanceDataFacilityCalculationParameters() {
        final PerformanceDataFacilityProcessingRequestPayload requestPayload =
                PerformanceDataFacilityProcessingRequestPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP8)
                        .reportType(PerformanceDataReportType.FINAL)
                        .targetPeriodYear(TargetPeriodYear.builder().targetYear(Year.of(2026)).build())
                        .targetPeriods(List.of(TargetPeriodDetailsDTO.builder()
                                .businessId(TargetPeriodType.TP8)
                                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                        .targetPeriodYears(List.of(
                                                TargetPeriodYear.builder().targetYear(Year.of(2025)).build(),
                                                TargetPeriodYear.builder().targetYear(Year.of(2026)).build()
                                        ))
                                        .build())
                                .build()))
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
                        .lastYearPerTp(Map.of(TargetPeriodType.TP8, 2026))
                        .build();
        // Invoke
        PerformanceDataFacilityCalculationParameters result = mapper.toPerformanceDataFacilityCalculationParameters(requestPayload);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilityCalculationParameters_with_INTERIM() {
        final PerformanceDataFacilityProcessingRequestPayload requestPayload =
                PerformanceDataFacilityProcessingRequestPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP8)
                        .reportType(PerformanceDataReportType.INTERIM)
                        .targetPeriodYear(TargetPeriodYear.builder().targetYear(Year.of(2026)).build())
                        .targetPeriods(List.of(TargetPeriodDetailsDTO.builder()
                                .businessId(TargetPeriodType.TP8)
                                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                        .targetPeriodYears(List.of(
                                                TargetPeriodYear.builder().targetYear(Year.of(2025)).build(),
                                                TargetPeriodYear.builder().targetYear(Year.of(2026)).build()
                                        ))
                                        .build())
                                .build()))
                        .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                                .baselineDate(LocalDate.of(2018, 1, 1))
                                .isTwelveMonths(true)
                                .energyCarbonFactor(BigDecimal.valueOf(0.01))
                                .measurementType(MeasurementType.ENERGY_KWH)
                                .usedReportingMechanism(true)
                                .improvements(Map.of(TargetImprovementType.TP8, BigDecimal.valueOf(100)))
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
                        .build();

        final PerformanceDataFacilityCalculationParameters expected =
                PerformanceDataFacilityCalculationParameters.builder()
                        .baselineDate(LocalDate.of(2018, 1, 1))
                        .isTwelveMonths(true)
                        .energyCarbonFactor(BigDecimal.valueOf(0.01))
                        .measurementType(MeasurementType.ENERGY_KWH)
                        .usedReportingMechanism(true)
                        .improvements(Map.of(TargetImprovementType.TP8, BigDecimal.valueOf(100)))
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
                        .lastYearPerTp(Map.of(TargetPeriodType.TP8, 2026))
                        .build();
        // Invoke
        PerformanceDataFacilityCalculationParameters result = mapper.toPerformanceDataFacilityCalculationParameters(requestPayload);

        // Verify
        assertThat(result).isEqualTo(expected);
    }
}
