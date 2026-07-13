package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEnergyFuelDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityReferenceData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormSubmitInitializerTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormSubmitInitializer initializer;

    @Mock
    private PerformanceDataFacilityReferenceDataService performanceDataFacilityDigitalFormReferenceDataService;

    @Mock
    private PerformanceDataFacilityStatusQueryService performanceDataFacilityStatusQueryService;

    @Test
    void initializePayload() {
        final Long accountId = 1L;
        final Long facilityId = 22L;
        final String facilityBusinessId = "facilityBusinessId";
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final Year reportYear = Year.of(2018);
        final FacilityBaseInfoDTO facility = FacilityBaseInfoDTO.builder()
                .id(facilityId)
                .facilityBusinessId(facilityBusinessId)
                .build();
        final Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build())
                )
                .payload(PerformanceDataFacilityDigitalFormRequestPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(reportYear)
                        .facility(facility)
                        .build())
                .build();

        final PerformanceDataFacilityReferenceData referenceData = PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                        .usedReportingMechanism(true)
                        .build())
                .build();
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload expected =
                PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_PAYLOAD)
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(reportYear)
                        .facility(facility)
                        .referenceData(referenceData)
                        .build();

        when(performanceDataFacilityDigitalFormReferenceDataService.getReferenceData(accountId, facilityBusinessId, reportYear))
                .thenReturn(referenceData);
        when(performanceDataFacilityStatusQueryService
                .getLastUploadedPerformanceDataContainer(facilityId, reportYear))
                .thenReturn(Optional.empty());

        // Invoke
        RequestTaskPayload result = initializer.initializePayload(request);

        // Verify
        assertThat(result)
                .isInstanceOf(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
        verify(performanceDataFacilityDigitalFormReferenceDataService, times(1))
                .getReferenceData(accountId, facilityBusinessId, reportYear);
        verify(performanceDataFacilityStatusQueryService, times(1))
                .getLastUploadedPerformanceDataContainer(facilityId, reportYear);
    }

    @Test
    void initializePayload_with_uploaded_performance_data() {
        final Long accountId = 1L;
        final Long facilityId = 22L;
        final String facilityBusinessId = "facilityBusinessId";
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final Year reportYear = Year.of(2018);
        final FacilityBaseInfoDTO facility = FacilityBaseInfoDTO.builder()
                .id(facilityId)
                .facilityBusinessId(facilityBusinessId)
                .build();
        final Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build())
                )
                .payload(PerformanceDataFacilityDigitalFormRequestPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(reportYear)
                        .facility(facility)
                        .build())
                .build();

        final PerformanceDataFacilityReferenceData referenceData = PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                        .usedReportingMechanism(true)
                        .build())
                .build();
        final PerformanceDataFacilityThroughputDetails throughputDetails = PerformanceDataFacilityThroughputDetails.builder()
                .actualThroughput(BigDecimal.TEN)
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
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload expected =
                PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_PAYLOAD)
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(reportYear)
                        .facility(facility)
                        .referenceData(referenceData)
                        .performanceData(PerformanceDataFacilityInputData.builder()
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
                                .build())
                        .build();

        when(performanceDataFacilityDigitalFormReferenceDataService.getReferenceData(accountId, facilityBusinessId, reportYear))
                .thenReturn(referenceData);
        when(performanceDataFacilityStatusQueryService
                .getLastUploadedPerformanceDataContainer(facilityId, reportYear))
                .thenReturn(Optional.of(container));

        // Invoke
        RequestTaskPayload result = initializer.initializePayload(request);

        // Verify
        assertThat(result)
                .isInstanceOf(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
        verify(performanceDataFacilityDigitalFormReferenceDataService, times(1))
                .getReferenceData(accountId, facilityBusinessId, reportYear);
        verify(performanceDataFacilityStatusQueryService, times(1))
                .getLastUploadedPerformanceDataContainer(facilityId, reportYear);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT);
    }
}