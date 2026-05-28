package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEnergyFuelDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityReferenceData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmissionDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmittedRequestActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilitySubmittedMapperTest {

    private final PerformanceDataFacilitySubmittedMapper mapper = Mappers.getMapper(PerformanceDataFacilitySubmittedMapper.class);

    @Test
    void toPerformanceDataFacility() {
        final PerformanceDataFacilityFixedConversionFactor standardFuel = PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY;
        final PerformanceDataFacilityBaselineAndTargets baselineTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .measurementType(MeasurementType.ENERGY_MWH)
                .usedReportingMechanism(true)
                .build();
        final PerformanceDataFacilityThroughputDetails throughputDetails = PerformanceDataFacilityThroughputDetails.builder()
                .actualThroughput(BigDecimal.TEN)
                .build();
        final PerformanceDataFacilityCalculatedResults calculatedResults = PerformanceDataFacilityCalculatedResults.builder()
                .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .build();
        final PerformanceDataFacilityRequestPayload requestPayload = PerformanceDataFacilityRequestPayload.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .reportType(PerformanceDataReportType.FINAL)
                .targetPeriodYear(Year.of(2026))
                .facility(FacilityBaseInfoDTO.builder().id(1L).build())
                .reportVersion(1)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .referenceData(PerformanceDataFacilityReferenceData.builder()
                        .baselineAndTargets(baselineTargets)
                        .build())
                .performanceData(PerformanceDataFacilityInputData.builder()
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
                        .build())
                .build();

        final PerformanceDataFacility expected = PerformanceDataFacility.builder()
                .data(PerformanceDataFacilityContainer.builder()
                        .baselineAndTargets(baselineTargets)
                        .energyFuelDetails(PerformanceDataFacilityEnergyFuelDetails.builder()
                                .fuels(List.of(
                                        PerformanceDataFacilityFuel.builder()
                                                .name(standardFuel.getDescription())
                                                .fixedConversionFactorCode(standardFuel)
                                                .conversionFactor(standardFuel.getFactor().setScale(5, RoundingMode.HALF_UP))
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
                        .build())
                .targetPeriodType(TargetPeriodType.TP7)
                .targetPeriodYear(Year.of(2026))
                .facilityId(1L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .build();

        // Invoke
        PerformanceDataFacility result = mapper.toPerformanceDataFacility(requestPayload);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toPerformanceDataFacilitySubmittedRequestActionPayload() {
        final PerformanceDataFacilityFixedConversionFactor standardFuel = PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY;
        final PerformanceDataFacilityBaselineAndTargets baselineTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .measurementType(MeasurementType.ENERGY_MWH)
                .usedReportingMechanism(true)
                .build();
        final PerformanceDataFacilityThroughputDetails throughputDetails = PerformanceDataFacilityThroughputDetails.builder()
                .actualThroughput(BigDecimal.TEN)
                .build();
        final PerformanceDataFacilityCalculatedResults calculatedResults = PerformanceDataFacilityCalculatedResults.builder()
                .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .build();
        final Request request = Request.builder()
                .payload(PerformanceDataFacilityRequestPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .reportType(PerformanceDataReportType.FINAL)
                        .targetPeriodYear(Year.of(2026))
                        .facility(FacilityBaseInfoDTO.builder().id(1L).build())
                        .reportVersion(1)
                        .submissionType(PerformanceDataSubmissionType.PRIMARY)
                        .referenceData(PerformanceDataFacilityReferenceData.builder()
                                .baselineAndTargets(baselineTargets)
                                .build())
                        .performanceData(PerformanceDataFacilityInputData.builder()
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
                                .build())
                        .build())
                .creationDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                .submissionDate(LocalDate.of(2026, 2, 2).atStartOfDay())
                .build();

        final PerformanceDataFacilitySubmittedRequestActionPayload expected = PerformanceDataFacilitySubmittedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.PERFORMANCE_DATA_FACILITY_SUBMITTED_PAYLOAD)
                .details(PerformanceDataFacilitySubmissionDetails.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .reportType(PerformanceDataReportType.FINAL)
                        .targetPeriodYear(Year.of(2026))
                        .submissionType(PerformanceDataSubmissionType.PRIMARY)
                        .reportVersion(1)
                        .creationDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                        .submissionDate(LocalDate.of(2026, 2, 2).atStartOfDay())
                        .build())
                .performanceData(PerformanceDataFacilityContainer.builder()
                        .baselineAndTargets(baselineTargets)
                        .energyFuelDetails(PerformanceDataFacilityEnergyFuelDetails.builder()
                                .fuels(List.of(
                                        PerformanceDataFacilityFuel.builder()
                                                .name(standardFuel.getDescription())
                                                .fixedConversionFactorCode(standardFuel)
                                                .conversionFactor(standardFuel.getFactor().setScale(5, RoundingMode.HALF_UP))
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
                        .build())
                .build();
        // Invoke
        PerformanceDataFacilitySubmittedRequestActionPayload result = mapper
                .toPerformanceDataFacilitySubmittedRequestActionPayload(request);

        // Verify
        assertThat(result).isEqualTo(expected);
    }
}
