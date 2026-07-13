package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEnergyFuelDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityReferenceData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmissionDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormSubmitServiceTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormSubmitService performanceDataFacilityDigitalFormSubmitService;

    @Mock
    private RequestService requestService;

    @Mock
    private PerformanceDataFacilityReferenceDataService performanceDataFacilityReferenceDataService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private PerformanceDataFacilityStatusService performanceDataFacilityStatusService;

    @Test
    void applySave() {
        RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .performanceData(PerformanceDataFacilityInputData.builder().build())
                        .build())
                .build();
        final PerformanceDataFacilityThroughputDetails throughputDetails = PerformanceDataFacilityThroughputDetails.builder()
                .actualThroughput(BigDecimal.TEN)
                .build();
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload actionPayload =
                PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload.builder()
                        .throughputDetails(throughputDetails)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        // Invoke
        performanceDataFacilityDigitalFormSubmitService.applySave(actionPayload, requestTask);

        // Verify
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload actual =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getPerformanceData()).isEqualTo(PerformanceDataFacilityInputData.builder().throughputDetails(throughputDetails).build());
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void applySave_no_performance_data() {
        RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder().build())
                .build();
        final PerformanceDataFacilityThroughputDetails throughputDetails = PerformanceDataFacilityThroughputDetails.builder()
                .actualThroughput(BigDecimal.TEN)
                .build();
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload actionPayload =
                PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload.builder()
                        .throughputDetails(throughputDetails)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        // Invoke
        performanceDataFacilityDigitalFormSubmitService.applySave(actionPayload, requestTask);

        // Verify
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload actual =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getPerformanceData()).isEqualTo(PerformanceDataFacilityInputData.builder().throughputDetails(throughputDetails).build());
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void cancel() {
        final String assignee = "assignee";
        final AppUser appUser = AppUser.builder().userId(assignee).build();
        final Request request = Request.builder()
                .payload(PerformanceDataFacilityDigitalFormRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder().request(request).build();

        // Invoke
        performanceDataFacilityDigitalFormSubmitService.cancel(appUser, requestTask);

        // Verify
        verify(requestService, times(1)).addActionToRequest(request, null,
                CcaRequestActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CANCELLED, assignee);
    }

    @Test
    void refreshBaselineData() {
        final String facilityBusinessId = "facilityBusinessId";
        final Year targetPeriodYear = Year.of(2018);
        final Long accountId = 1L;
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodYear(targetPeriodYear)
                        .facility(FacilityBaseInfoDTO.builder().facilityBusinessId(facilityBusinessId).build())
                        .referenceData(PerformanceDataFacilityReferenceData.builder()
                                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                                        .measurementType(MeasurementType.ENERGY_KWH)
                                        .baselineDate(LocalDate.of(2018, 1, 1))
                                        .build())
                                .build())
                        .performanceData(PerformanceDataFacilityInputData.builder()
                                .calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
                                        .actualCo2Emissions(BigDecimal.ZERO)
                                        .build())
                                .build())
                        .sectionsCompleted(Map.of("subtask", "in_progress"))
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType(ResourceType.ACCOUNT)
                                .resourceId(accountId.toString())
                                .build())
                        )
                        .build())
                .payload(taskPayload)
                .build();

        final PerformanceDataFacilityBaselineAndTargets baselineData = PerformanceDataFacilityBaselineAndTargets.builder()
                .measurementType(MeasurementType.ENERGY_GJ)
                .build();

        when(performanceDataFacilityReferenceDataService
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear))
                .thenReturn(baselineData);

        // Invoke
        performanceDataFacilityDigitalFormSubmitService.refreshBaselineData(requestTask);

        // Verify
        assertThat(taskPayload.getSectionsCompleted()).isEmpty();
        assertThat(taskPayload.getReferenceData().getBaselineAndTargets()).isEqualTo(baselineData);
        assertThat(taskPayload.getPerformanceData().getCalculatedResults()).isNull();
        verify(performanceDataFacilityReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear);
    }

    @Test
    void refreshBaselineData_no_data() {
        final String facilityBusinessId = "facilityBusinessId";
        final Year targetPeriodYear = Year.of(2018);
        final Long accountId = 1L;
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodYear(targetPeriodYear)
                        .facility(FacilityBaseInfoDTO.builder().facilityBusinessId(facilityBusinessId).build())
                        .referenceData(PerformanceDataFacilityReferenceData.builder()
                                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                                        .measurementType(MeasurementType.ENERGY_KWH)
                                        .baselineDate(LocalDate.of(2018, 1, 1))
                                        .build())
                                .build())
                        .sectionsCompleted(Map.of("subtask", "in_progress"))
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType(ResourceType.ACCOUNT)
                                .resourceId(accountId.toString())
                                .build())
                        )
                        .build())
                .payload(taskPayload)
                .build();

        final PerformanceDataFacilityBaselineAndTargets baselineData = PerformanceDataFacilityBaselineAndTargets.builder()
                .measurementType(MeasurementType.ENERGY_GJ)
                .build();

        when(performanceDataFacilityReferenceDataService
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear))
                .thenReturn(baselineData);

        // Invoke
        performanceDataFacilityDigitalFormSubmitService.refreshBaselineData(requestTask);

        // Verify
        assertThat(taskPayload.getSectionsCompleted()).isEmpty();
        assertThat(taskPayload.getReferenceData().getBaselineAndTargets()).isEqualTo(baselineData);
        assertThat(taskPayload.getPerformanceData()).isNull();
        verify(performanceDataFacilityReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear);
    }

    @Test
    void calculate() {
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload = PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .reportType(PerformanceDataReportType.FINAL)
                .targetPeriodYear(Year.of(2018))
                .referenceData(PerformanceDataFacilityReferenceData.builder()
                        .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
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
                                .build())
                        .build())
                .performanceData(PerformanceDataFacilityInputData.builder()
                        .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                                .standardFuels(Map.of(
                                        PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY,
                                        PerformanceDataFacilityFuelEnergyConsumption.builder()
                                                .deliveredEnergy(BigDecimal.valueOf(1800000))
                                                .build()
                                ))
                                .electricitySuppliedFromCHP(BigDecimal.valueOf(100000))
                                .build())
                        .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                                .actualThroughput(BigDecimal.valueOf(5000))
                                .build())
                        .build())
                .build();
        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(TargetPeriodYear.builder().targetYear(Year.of(2018)).build()))
                                .build())
                        .build()
        );

        when(targetPeriodService
                .getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9)))
                .thenReturn(targetPeriods);

        // Invoke
        performanceDataFacilityDigitalFormSubmitService.calculate(requestTask);

        // Verify
        assertThat(taskPayload.getPerformanceData().getCalculatedResults()).isNotNull();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
    }

    @Test
    void markTaskAsExpired() {
        final String assignee = "assignee";
        final AppUser appUser = AppUser.builder().userId(assignee).build();
        final LocalDateTime submissionDate = LocalDateTime.now();
        final Request request = Request.builder().id("request-id").build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder().build())
                .build();

        // Invoke
        performanceDataFacilityDigitalFormSubmitService.markTaskAsExpired(appUser, requestTask, submissionDate);

        // Verify
        PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload actual =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.isExpired()).isTrue();
        assertThat(requestTask.getRequest().getSubmissionDate()).isEqualTo(submissionDate);
        verify(requestService, times(1)).addActionToRequest(request, null,
                CcaRequestActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_EXPIRED, assignee);
    }

    @Test
    void submit() {
        final String assignee = "assignee";
        final AppUser appUser = AppUser.builder().userId(assignee).build();
        final LocalDateTime submissionDate = LocalDate.of(2025, 3, 1).atStartOfDay();
        final LocalDateTime creationDate = LocalDate.of(2025, 1, 1).atStartOfDay();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP8;
        final int reportVersion = 2;
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .usedReportingMechanism(true)
                .build();
        final PerformanceDataFacilityReferenceData referenceData = PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(baselineAndTargets)
                .build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .electricitySuppliedFromCHP(BigDecimal.TEN)
                        .build())
                .build();
        PerformanceDataFacilityDigitalFormRequestPayload requestPayload = PerformanceDataFacilityDigitalFormRequestPayload.builder()
                .targetPeriodType(targetPeriodType)
                .reportType(PerformanceDataReportType.FINAL)
                .targetPeriodYear(Year.of(2018))
                .build();
        PerformanceDataFacilityDigitalFormRequestMetadata metadata = PerformanceDataFacilityDigitalFormRequestMetadata.builder().build();
        Request request = Request.builder()
                .payload(requestPayload)
                .metadata(metadata)
                .creationDate(creationDate)
                .build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .referenceData(referenceData)
                        .performanceData(performanceData)
                        .build())
                .build();

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .secondaryReportingStartDate(LocalDate.of(2025, 7, 1))
                .build();
        final PerformanceDataFacilitySubmittedRequestActionPayload actionPayload =
                PerformanceDataFacilitySubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.PERFORMANCE_DATA_FACILITY_SUBMITTED_PAYLOAD)
                        .details(PerformanceDataFacilitySubmissionDetails.builder()
                                .targetPeriodType(targetPeriodType)
                                .reportType(PerformanceDataReportType.FINAL)
                                .targetPeriodYear(Year.of(2018))
                                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                                .reportVersion(reportVersion)
                                .submissionDate(submissionDate)
                                .creationDate(creationDate)
                                .build())
                        .performanceData(PerformanceDataFacilityContainer.builder()
                                .baselineAndTargets(baselineAndTargets)
                                .energyFuelDetails(PerformanceDataFacilityEnergyFuelDetails.builder()
                                        .electricitySuppliedFromCHP(BigDecimal.TEN)
                                        .build())
                                .build())
                        .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(performanceDataFacilityStatusService.submitPerformanceData(any()))
                .thenReturn(reportVersion);

        // Invoke
        performanceDataFacilityDigitalFormSubmitService.submit(appUser, requestTask, submissionDate);

        // Verify
        assertThat(metadata.getSubmissionType()).isEqualTo(PerformanceDataSubmissionType.PRIMARY);
        assertThat(metadata.getReportVersion()).isEqualTo(reportVersion);
        assertThat(metadata.getSubmittedDate()).isEqualTo(submissionDate.toLocalDate());
        assertThat(requestPayload.getSubmissionType()).isEqualTo(PerformanceDataSubmissionType.PRIMARY);
        assertThat(requestPayload.getReportVersion()).isEqualTo(reportVersion);
        assertThat(requestPayload.getReferenceData()).isEqualTo(referenceData);
        assertThat(requestPayload.getPerformanceData()).isEqualTo(performanceData);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(performanceDataFacilityStatusService, times(1)).submitPerformanceData(any());
        verify(requestService, times(1)).addActionToRequest(request, actionPayload,
                CcaRequestActionType.PERFORMANCE_DATA_FACILITY_SUBMITTED, assignee);
    }

    @Test
    void submit_for_INTERIM() {
        final String assignee = "assignee";
        final AppUser appUser = AppUser.builder().userId(assignee).build();
        final LocalDateTime submissionDate = LocalDate.of(2025, 3, 1).atStartOfDay();
        final LocalDateTime creationDate = LocalDate.of(2025, 1, 1).atStartOfDay();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP8;
        final int reportVersion = 2;
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .usedReportingMechanism(true)
                .build();
        final PerformanceDataFacilityReferenceData referenceData = PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(baselineAndTargets)
                .build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .energyFuelDetails(PerformanceDataFacilityInputEnergyFuelDetails.builder()
                        .electricitySuppliedFromCHP(BigDecimal.TEN)
                        .build())
                .build();
        PerformanceDataFacilityDigitalFormRequestPayload requestPayload = PerformanceDataFacilityDigitalFormRequestPayload.builder()
                .targetPeriodType(targetPeriodType)
                .reportType(PerformanceDataReportType.INTERIM)
                .targetPeriodYear(Year.of(2018))
                .build();
        PerformanceDataFacilityDigitalFormRequestMetadata metadata = PerformanceDataFacilityDigitalFormRequestMetadata.builder().build();
        Request request = Request.builder()
                .payload(requestPayload)
                .metadata(metadata)
                .creationDate(creationDate)
                .build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .referenceData(referenceData)
                        .performanceData(performanceData)
                        .build())
                .build();

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .secondaryReportingStartDate(LocalDate.of(2025, 7, 1))
                .build();
        final PerformanceDataFacilitySubmittedRequestActionPayload actionPayload =
                PerformanceDataFacilitySubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.PERFORMANCE_DATA_FACILITY_SUBMITTED_PAYLOAD)
                        .details(PerformanceDataFacilitySubmissionDetails.builder()
                                .targetPeriodType(targetPeriodType)
                                .reportType(PerformanceDataReportType.INTERIM)
                                .targetPeriodYear(Year.of(2018))
                                .reportVersion(reportVersion)
                                .submissionDate(submissionDate)
                                .creationDate(creationDate)
                                .build())
                        .performanceData(PerformanceDataFacilityContainer.builder()
                                .baselineAndTargets(baselineAndTargets)
                                .energyFuelDetails(PerformanceDataFacilityEnergyFuelDetails.builder()
                                        .electricitySuppliedFromCHP(BigDecimal.TEN)
                                        .build())
                                .build())
                        .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(performanceDataFacilityStatusService.submitPerformanceData(any()))
                .thenReturn(reportVersion);

        // Invoke
        performanceDataFacilityDigitalFormSubmitService.submit(appUser, requestTask, submissionDate);

        // Verify
        assertThat(metadata.getSubmissionType()).isNull();
        assertThat(metadata.getReportVersion()).isEqualTo(reportVersion);
        assertThat(metadata.getSubmittedDate()).isEqualTo(submissionDate.toLocalDate());
        assertThat(requestPayload.getSubmissionType()).isNull();
        assertThat(requestPayload.getReportVersion()).isEqualTo(reportVersion);
        assertThat(requestPayload.getReferenceData()).isEqualTo(referenceData);
        assertThat(requestPayload.getPerformanceData()).isEqualTo(performanceData);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(performanceDataFacilityStatusService, times(1)).submitPerformanceData(any());
        verify(requestService, times(1)).addActionToRequest(request, actionPayload,
                CcaRequestActionType.PERFORMANCE_DATA_FACILITY_SUBMITTED, assignee);
    }
}
