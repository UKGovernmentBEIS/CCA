package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain;

import org.junit.jupiter.api.Test;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PerformanceDataFacilityBaselineAndTargetsTest {

    @Test
    void equals_valid() {
        final PerformanceDataFacilityBaselineAndTargets data = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2018, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(Boolean.TRUE)
                .improvements(Map.of(
                        TargetImprovementType.TP8, BigDecimal.valueOf(50.32),
                        TargetImprovementType.TP7, BigDecimal.valueOf(40.32)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10.32))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(BigDecimal.valueOf(20.32))
                .totalThroughput(BigDecimal.valueOf(30.32))
                .throughputUnit("unit")
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 1")
                                .baselineYear(Year.of(2018))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(60.32))
                                .throughput(BigDecimal.valueOf(70.32))
                                .throughputUnit("unit1")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 2")
                                .baselineYear(Year.of(2019))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(80.32))
                                .throughput(BigDecimal.valueOf(90.32))
                                .throughputUnit("unit2")
                                .build()
                ))
                .build();

        final PerformanceDataFacilityBaselineAndTargets dataCompared = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2018, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(Boolean.TRUE)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(40.32).setScale(10, RoundingMode.HALF_UP),
                        TargetImprovementType.TP8, BigDecimal.valueOf(50.32).setScale(10, RoundingMode.HALF_UP)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10.32).setScale(10, RoundingMode.HALF_UP))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(BigDecimal.valueOf(20.32).setScale(10, RoundingMode.HALF_UP))
                .totalThroughput(BigDecimal.valueOf(30.32).setScale(10, RoundingMode.HALF_UP))
                .throughputUnit("unit")
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 2")
                                .baselineYear(Year.of(2019))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(80.32).setScale(10, RoundingMode.HALF_UP))
                                .throughput(BigDecimal.valueOf(90.32).setScale(10, RoundingMode.HALF_UP))
                                .throughputUnit("unit2")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 1")
                                .baselineYear(Year.of(2018))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(60.32).setScale(10, RoundingMode.HALF_UP))
                                .throughput(BigDecimal.valueOf(70.32).setScale(10, RoundingMode.HALF_UP))
                                .throughputUnit("unit1")
                                .build()
                ))
                .build();

        assertThat(data.equals(dataCompared)).isTrue();
    }

    @Test
    void equals_no_products_valid() {
        final PerformanceDataFacilityBaselineAndTargets data = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2018, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(Boolean.TRUE)
                .improvements(Map.of(
                        TargetImprovementType.TP8, BigDecimal.valueOf(50.32),
                        TargetImprovementType.TP7, BigDecimal.valueOf(40.32)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10.32))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(20.32))
                .totalThroughput(BigDecimal.valueOf(30.32))
                .throughputUnit("unit")
                .build();

        final PerformanceDataFacilityBaselineAndTargets dataCompared = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2018, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(Boolean.TRUE)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(40.32).setScale(10, RoundingMode.HALF_UP),
                        TargetImprovementType.TP8, BigDecimal.valueOf(50.32).setScale(10, RoundingMode.HALF_UP)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10.32).setScale(10, RoundingMode.HALF_UP))
                .variableEnergyType(VariableEnergyDepictionType.TOTALS)
                .baselineVariableEnergy(BigDecimal.valueOf(20.32).setScale(10, RoundingMode.HALF_UP))
                .totalThroughput(BigDecimal.valueOf(30.32).setScale(10, RoundingMode.HALF_UP))
                .throughputUnit("unit")
                .build();

        assertThat(data.equals(dataCompared)).isTrue();
    }

    @Test
    void equals_no_variable_energy_valid() {
        final PerformanceDataFacilityBaselineAndTargets data = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2018, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(Boolean.TRUE)
                .improvements(Map.of(
                        TargetImprovementType.TP8, BigDecimal.valueOf(50.32),
                        TargetImprovementType.TP7, BigDecimal.valueOf(40.32)
                ))
                .build();

        final PerformanceDataFacilityBaselineAndTargets dataCompared = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2018, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(Boolean.TRUE)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(40.32).setScale(10, RoundingMode.HALF_UP),
                        TargetImprovementType.TP8, BigDecimal.valueOf(50.32).setScale(10, RoundingMode.HALF_UP)
                ))
                .build();

        assertThat(data.equals(dataCompared)).isTrue();
    }

    @Test
    void equals_empty_valid() {
        final PerformanceDataFacilityBaselineAndTargets data = PerformanceDataFacilityBaselineAndTargets.builder().build();
        final PerformanceDataFacilityBaselineAndTargets dataCompared = PerformanceDataFacilityBaselineAndTargets.builder().build();

        assertThat(data.equals(dataCompared)).isTrue();
    }

    @Test
    void equals_more_products_not_valid() {
        final PerformanceDataFacilityBaselineAndTargets data = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2018, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(Boolean.TRUE)
                .improvements(Map.of(
                        TargetImprovementType.TP8, BigDecimal.valueOf(50.32),
                        TargetImprovementType.TP7, BigDecimal.valueOf(40.32)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10.32))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(BigDecimal.valueOf(20.32))
                .totalThroughput(BigDecimal.valueOf(30.32))
                .throughputUnit("unit")
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 1")
                                .baselineYear(Year.of(2018))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(60.32))
                                .throughput(BigDecimal.valueOf(70.32))
                                .throughputUnit("unit1")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 2")
                                .baselineYear(Year.of(2019))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(80.32))
                                .throughput(BigDecimal.valueOf(90.32))
                                .throughputUnit("unit2")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 3")
                                .baselineYear(Year.of(2019))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(80.32))
                                .throughput(BigDecimal.valueOf(90.32))
                                .throughputUnit("unit2")
                                .build()
                ))
                .build();

        final PerformanceDataFacilityBaselineAndTargets dataCompared = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2018, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(Boolean.TRUE)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(40.32).setScale(10, RoundingMode.HALF_UP),
                        TargetImprovementType.TP8, BigDecimal.valueOf(50.32).setScale(10, RoundingMode.HALF_UP)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10.32).setScale(10, RoundingMode.HALF_UP))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(BigDecimal.valueOf(20.32).setScale(10, RoundingMode.HALF_UP))
                .totalThroughput(BigDecimal.valueOf(30.32).setScale(10, RoundingMode.HALF_UP))
                .throughputUnit("unit")
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 2")
                                .baselineYear(Year.of(2019))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(80.32).setScale(10, RoundingMode.HALF_UP))
                                .throughput(BigDecimal.valueOf(90.32).setScale(10, RoundingMode.HALF_UP))
                                .throughputUnit("unit2")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 1")
                                .baselineYear(Year.of(2018))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(60.32).setScale(10, RoundingMode.HALF_UP))
                                .throughput(BigDecimal.valueOf(70.32).setScale(10, RoundingMode.HALF_UP))
                                .throughputUnit("unit1")
                                .build()
                ))
                .build();

        assertThat(data.equals(dataCompared)).isFalse();
    }

    @Test
    void equals_different_products_not_valid() {
        final PerformanceDataFacilityBaselineAndTargets data = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2018, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(Boolean.TRUE)
                .improvements(Map.of(
                        TargetImprovementType.TP8, BigDecimal.valueOf(50.32),
                        TargetImprovementType.TP7, BigDecimal.valueOf(40.32)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10.32))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(BigDecimal.valueOf(20.32))
                .totalThroughput(BigDecimal.valueOf(30.32))
                .throughputUnit("unit")
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 1")
                                .baselineYear(Year.of(2018))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(60.32))
                                .throughput(BigDecimal.valueOf(70.32))
                                .throughputUnit("unit1")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 2")
                                .baselineYear(Year.of(2019))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(80.32))
                                .throughput(BigDecimal.valueOf(90.32))
                                .throughputUnit("unit2")
                                .build()
                ))
                .build();

        final PerformanceDataFacilityBaselineAndTargets dataCompared = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2018, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(Boolean.TRUE)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(40.32).setScale(10, RoundingMode.HALF_UP),
                        TargetImprovementType.TP8, BigDecimal.valueOf(50.32).setScale(10, RoundingMode.HALF_UP)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(10.32).setScale(10, RoundingMode.HALF_UP))
                .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                .baselineVariableEnergy(BigDecimal.valueOf(20.32).setScale(10, RoundingMode.HALF_UP))
                .totalThroughput(BigDecimal.valueOf(30.32).setScale(10, RoundingMode.HALF_UP))
                .throughputUnit("unit")
                .variableEnergyConsumptionDataByProduct(List.of(
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 2")
                                .baselineYear(Year.of(2019))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(80.322).setScale(10, RoundingMode.HALF_UP))
                                .throughput(BigDecimal.valueOf(90.32).setScale(10, RoundingMode.HALF_UP))
                                .throughputUnit("unit2")
                                .build(),
                        ProductVariableEnergyConsumptionData.builder()
                                .productName("Product Name 1")
                                .baselineYear(Year.of(2018))
                                .productStatus(ProductStatus.LIVE)
                                .energy(BigDecimal.valueOf(60.32).setScale(10, RoundingMode.HALF_UP))
                                .throughput(BigDecimal.valueOf(70.32).setScale(10, RoundingMode.HALF_UP))
                                .throughputUnit("unit1")
                                .build()
                ))
                .build();

        assertThat(data.equals(dataCompared)).isFalse();
    }
}
