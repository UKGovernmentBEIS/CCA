package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEnergyFuelDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmissionDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingResults;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityProcessingSubmittedMapperTest {

    private final PerformanceDataFacilityProcessingSubmittedMapper mapper = Mappers.getMapper(PerformanceDataFacilityProcessingSubmittedMapper.class);

    @Test
    void toPerformanceDataFacilitySubmittedRequestActionPayload() {
        final PerformanceDataFacilityContainer container = PerformanceDataFacilityContainer.builder()
                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                        .measurementType(MeasurementType.ENERGY_MWH)
                        .usedReportingMechanism(true)
                        .build())
                .energyFuelDetails(PerformanceDataFacilityEnergyFuelDetails.builder()
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(22))
                        .throughputAdjustmentFactor(BigDecimal.valueOf(33))
                        .build())
                .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                        .actualThroughput(BigDecimal.TEN)
                        .build())
                .calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
                        .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .build())
                .build();
        final PerformanceDataFacilityProcessingResults processingResults = PerformanceDataFacilityProcessingResults.builder()
                .container(container)
                .reportVersion(1)
                .build();
        final Request request = Request.builder()
                .payload(PerformanceDataFacilityProcessingRequestPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .reportType(PerformanceDataReportType.FINAL)
                        .submissionType(PerformanceDataSubmissionType.PRIMARY)
                        .submissionDate(LocalDate.of(2026, 2, 2).atStartOfDay())
                        .targetPeriodYear(TargetPeriodYear.builder().targetYear(Year.of(2026)).build())
                        .facility(FacilityDTO.builder().id(1L).build())
                        .build())
                .creationDate(LocalDate.of(2026, 1, 1).atStartOfDay())
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
                .performanceData(container)
                .build();
        // Invoke
        PerformanceDataFacilitySubmittedRequestActionPayload result = mapper
                .toPerformanceDataFacilitySubmittedRequestActionPayload(request, processingResults);

        // Verify
        assertThat(result).isEqualTo(expected);
    }
}
