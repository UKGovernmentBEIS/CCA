package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityValidator;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation.PerformanceDataFacilityInputDataValidator;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityUploadCsvData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.validation.PerformanceDataFacilityProcessingValidator;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityProcessingServiceTest {

    @InjectMocks
    private PerformanceDataFacilityProcessingService performanceDataFacilityProcessingService;

    @Mock
    private RequestService requestService;

    @Mock
    private PerformanceDataFacilityInputDataValidator performanceDataFacilityInputDataValidator;

    @Mock
    private PerformanceDataFacilityProcessingValidator performanceDataFacilityProcessingValidator;

    @Mock
    private PerformanceDataFacilityValidator performanceDataFacilityValidator;

    @Mock
    private PerformanceDataFacilityStatusService performanceDataFacilityStatusService;

    @Test
    void doProcess() throws BpmnExecutionException {
        final FacilityDTO facility = FacilityDTO.builder().id(1L).build();
        final TargetPeriodYear targetPeriodYear = TargetPeriodYear.builder().targetYear(Year.of(2026)).build();
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(30000000))
                .build();
        final PerformanceDataFacilityProcessingRequestPayload requestPayload =
                PerformanceDataFacilityProcessingRequestPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .reportType(PerformanceDataReportType.FINAL)
                        .facility(facility)
                        .targetPeriodYear(targetPeriodYear)
                        .baselineAndTargets(baselineAndTargets)
                        .targetPeriods(List.of(
                                TargetPeriodDetailsDTO.builder()
                                        .businessId(TargetPeriodType.TP7)
                                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                                .targetPeriodYears(List.of(
                                                        TargetPeriodYear.builder().targetYear(Year.of(2026)).build()
                                                ))
                                                .build())
                                        .build()
                        ))
                        .build();
        FacilityUploadReport facilityUploadReport = FacilityUploadReport.builder()
                .csvData(PerformanceDataFacilityUploadCsvData.builder()
                        .gridElectricity(BigDecimal.valueOf(10000000))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        List<BusinessValidationResult> results = new ArrayList<>();
        results.add(BusinessValidationResult.valid());
        final int reportVersion = 2;

        when(performanceDataFacilityInputDataValidator.validateData(any(), any()))
                .thenReturn(results);
        when(performanceDataFacilityProcessingValidator.validateCsvRules(any(), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityValidator.validateFacilityEligibility(facility, targetPeriodYear))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityValidator.validateFacilityBaselineDateEligibility(targetPeriodYear, baselineAndTargets))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityValidator.validateFacilityProductsEligibility(targetPeriodYear, baselineAndTargets))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityValidator.validateFacilityReportingLock(facility, targetPeriodYear))
        		.thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityStatusService.submitPerformanceData(any()))
                .thenReturn(reportVersion);

        // Invoke
        performanceDataFacilityProcessingService.doProcess(requestPayload, facilityUploadReport);

        // Verify
        assertThat(facilityUploadReport.getErrors()).isEmpty();
        verify(performanceDataFacilityInputDataValidator, times(1))
                .validateData(any(), any());
        verify(performanceDataFacilityProcessingValidator, times(1))
                .validateCsvRules(any(), any());
        verify(performanceDataFacilityValidator, times(1))
                .validateFacilityEligibility(facility, targetPeriodYear);
        verify(performanceDataFacilityValidator, times(1))
                .validateFacilityBaselineDateEligibility(targetPeriodYear, baselineAndTargets);
        verify(performanceDataFacilityValidator, times(1))
                .validateFacilityProductsEligibility(targetPeriodYear, baselineAndTargets);
        verify(performanceDataFacilityValidator, times(1))
        		.validateFacilityReportingLock(facility, targetPeriodYear);
        verify(performanceDataFacilityStatusService, times(1))
                .submitPerformanceData(any());
    }

    @Test
    void doProcess_validation_error() throws BpmnExecutionException {
        final FacilityDTO facility = FacilityDTO.builder().id(1L).build();
        final TargetPeriodYear targetPeriodYear = TargetPeriodYear.builder().targetYear(Year.of(2026)).build();
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .usedReportingMechanism(true)
                .improvements(Map.of(
                        TargetImprovementType.TP7, BigDecimal.valueOf(8),
                        TargetImprovementType.TP8, BigDecimal.valueOf(12),
                        TargetImprovementType.TP9, BigDecimal.valueOf(16)
                ))
                .totalFixedEnergy(BigDecimal.valueOf(30000000))
                .build();
        final PerformanceDataFacilityProcessingRequestPayload requestPayload =
                PerformanceDataFacilityProcessingRequestPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .reportType(PerformanceDataReportType.FINAL)
                        .facility(facility)
                        .targetPeriodYear(targetPeriodYear)
                        .baselineAndTargets(baselineAndTargets)
                        .targetPeriods(List.of(
                                TargetPeriodDetailsDTO.builder()
                                        .businessId(TargetPeriodType.TP7)
                                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                                .targetPeriodYears(List.of(
                                                        TargetPeriodYear.builder().targetYear(Year.of(2026)).build()
                                                ))
                                                .build())
                                        .build()
                        ))
                        .build();
        FacilityUploadReport facilityUploadReport = FacilityUploadReport.builder()
                .csvData(PerformanceDataFacilityUploadCsvData.builder()
                        .gridElectricity(BigDecimal.valueOf(10000000))
                        .atLeastSeventyPercentEnergyUsed(true)
                        .electricitySuppliedFromCHP(BigDecimal.valueOf(5000000))
                        .actualThroughput(BigDecimal.valueOf(10500))
                        .build())
                .build();

        List<BusinessValidationResult> results = new ArrayList<>();
        results.add(BusinessValidationResult.invalid(List.of(new BusinessViolation("error"))));

        when(performanceDataFacilityInputDataValidator.validateData(any(), any()))
                .thenReturn(results);
        when(performanceDataFacilityProcessingValidator.validateCsvRules(any(), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityValidator.validateFacilityEligibility(facility, targetPeriodYear))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityValidator.validateFacilityBaselineDateEligibility(targetPeriodYear, baselineAndTargets))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityValidator.validateFacilityProductsEligibility(targetPeriodYear, baselineAndTargets))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityValidator.validateFacilityReportingLock(facility, targetPeriodYear))
				.thenReturn(BusinessValidationResult.valid());

        // Invoke
        performanceDataFacilityProcessingService.doProcess(requestPayload, facilityUploadReport);

        // Verify
        assertThat(facilityUploadReport.getErrors()).isNotEmpty();
        verify(performanceDataFacilityInputDataValidator, times(1))
                .validateData(any(), any());
        verify(performanceDataFacilityProcessingValidator, times(1))
                .validateCsvRules(any(), any());
        verify(performanceDataFacilityValidator, times(1))
                .validateFacilityEligibility(facility, targetPeriodYear);
        verify(performanceDataFacilityValidator, times(1))
                .validateFacilityBaselineDateEligibility(targetPeriodYear, baselineAndTargets);
        verify(performanceDataFacilityValidator, times(1))
                .validateFacilityProductsEligibility(targetPeriodYear, baselineAndTargets);
        verify(performanceDataFacilityValidator, times(1))
				.validateFacilityReportingLock(facility, targetPeriodYear);
        verifyNoInteractions(performanceDataFacilityStatusService);
    }

    @Test
    void markAsCompleted() {
        final String requestId = "requestId";
        final String parentRequestId = "parentRequestId";
        final Request request = Request.builder()
                .payload(PerformanceDataFacilityProcessingRequestPayload.builder()
                        .parentRequestId(parentRequestId)
                        .build())
                .build();
        final PerformanceDataFacilityDataProcessingRequestPayload parentPayload =
                PerformanceDataFacilityDataProcessingRequestPayload.builder().build();
        final Request parentRequest = Request.builder()
                .payload(parentPayload)
                .build();
        final FacilityUploadReport facilityReport = FacilityUploadReport.builder().facilityId(1L).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);

        // Invoke
        performanceDataFacilityProcessingService.markAsCompleted(requestId, facilityReport);

        // Verify
        assertThat(facilityReport.isSucceeded()).isTrue();
        assertThat(parentPayload.getFacilityReports()).hasSize(1);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).findRequestById(parentRequestId);
    }
}
