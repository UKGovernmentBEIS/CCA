package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityContainerMapperTest {

    private final PerformanceDataFacilityContainerMapper mapper = Mappers.getMapper(PerformanceDataFacilityContainerMapper.class);

    @Test
    void toPerformanceDataFacilityContainer() {
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .measurementType(MeasurementType.ENERGY_MWH)
                .usedReportingMechanism(true)
                .build();
        final PerformanceDataFacilityThroughputDetails throughputDetails = PerformanceDataFacilityThroughputDetails.builder()
                .actualThroughput(BigDecimal.TEN)
                .build();
        final PerformanceDataFacilityCalculatedResults calculatedResults = PerformanceDataFacilityCalculatedResults.builder()
                .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .build();
        final PerformanceDataFacilityFixedConversionFactor standardFuel = PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY;
        final PerformanceDataFacilityInputData data = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                standardFuel,
                                PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.ONE)
                                        .primaryEnergy(BigDecimal.TWO)
                                        .build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .conversionFactor(BigDecimal.valueOf(100))
                                        .deliveredEnergy(BigDecimal.valueOf(101))
                                        .primaryEnergy(BigDecimal.valueOf(102))
                                        .build()
                        ))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(22))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(33))
                        .build())
                .throughputDetails(throughputDetails)
                .calculatedResults(calculatedResults)
                .build();

        PerformanceDataFacilityContainer expected = PerformanceDataFacilityContainer.builder()
                .baselineAndTargets(baselineAndTargets)
                .energyFuelDetails(PerformanceDataFacilityEnergyFuelDetails.builder()
                        .fuels(List.of(
                                PerformanceDataFacilityFuel.builder()
                                        .name(standardFuel.getDescription())
                                        .fixedConversionFactorCode(standardFuel)
                                        .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(standardFuel, baselineAndTargets.getMeasurementType()))
                                        .deliveredEnergy(BigDecimal.ONE)
                                        .primaryConversionFactor(standardFuel.getPrimaryFactor())
                                        .primaryEnergy(BigDecimal.TWO)
                                        .build(),
                                PerformanceDataFacilityFuel.builder()
                                        .name("Biofuel")
                                        .conversionFactor(BigDecimal.valueOf(100))
                                        .deliveredEnergy(BigDecimal.valueOf(101))
                                        .primaryConversionFactor(BigDecimal.ONE)
                                        .primaryEnergy(BigDecimal.valueOf(102))
                                        .build()
                        ))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(22))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(33))
                        .build())
                .throughputDetails(throughputDetails)
                .calculatedResults(calculatedResults)
                .build();

        // Invoke
        PerformanceDataFacilityContainer result = mapper.toPerformanceDataFacilityContainer(baselineAndTargets, data);

        // Verify
        assertThat(result).isEqualTo(expected);
    }
}
