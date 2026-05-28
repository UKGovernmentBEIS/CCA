package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityReferenceData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation.PerformanceDataFacilityInputDataValidator;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation.PerformanceDataFacilityDigitalFormValidator;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormSubmitValidatorTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormSubmitValidator validator;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private PerformanceDataFacilityDigitalFormValidator performanceDataFacilityDigitalFormValidator;

    @Mock
    private PerformanceDataFacilityReferenceDataService performanceDataFacilityDigitalFormReferenceDataService;

    @Mock
    private PerformanceDataFacilityInputDataValidator performanceDataFacilityDigitalFormDataValidator;

    @Mock
    private PerformanceDataFacilityDigitalFormInputCalculatedDataValidator performanceDataFacilityDigitalFormInputCalculatedDataValidator;

    @Mock
    private PerformanceDataFacilityDigitalFormCalculatedDataValidator performanceDataFacilityDigitalFormCalculatedDataValidator;

    @Test
    void isReportSubmissionExpired() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final Long facilityId = 1L;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final Year targetYear = Year.of(2018);
        final RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(targetYear)
                        .facility(FacilityBaseInfoDTO.builder().id(facilityId).build())
                        .build())
                .build();
        final LocalDate submissionDate = LocalDate.now();

        final TargetPeriodYear targetPeriodYear = TargetPeriodYear.builder().targetYear(targetYear).build();
        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .businessId(targetPeriodType)
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(targetPeriodYear))
                        .build())
                .build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(targetPeriod, reportType, submissionDate))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityEligibility(facility, targetPeriodYear))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        boolean result = validator.isReportSubmissionExpired(requestTask, submissionDate);

        // Verify
        assertThat(result).isFalse();
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(targetPeriod, reportType, submissionDate);
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityEligibility(facility, targetPeriodYear);
    }

    @Test
    void isReportSubmissionExpired_facility_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final Long facilityId = 1L;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final Year targetYear = Year.of(2018);
        final RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(targetYear)
                        .facility(FacilityBaseInfoDTO.builder().id(facilityId).build())
                        .build())
                .build();
        final LocalDate submissionDate = LocalDate.now();

        final TargetPeriodYear targetPeriodYear = TargetPeriodYear.builder().targetYear(targetYear).build();
        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .businessId(targetPeriodType)
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(targetPeriodYear))
                        .build())
                .build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(targetPeriod, reportType, submissionDate))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityEligibility(facility, targetPeriodYear))
                .thenReturn(BusinessValidationResult.invalid(List.of()));

        // Invoke
        boolean result = validator.isReportSubmissionExpired(requestTask, submissionDate);

        // Verify
        assertThat(result).isTrue();
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(targetPeriod, reportType, submissionDate);
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityEligibility(facility, targetPeriodYear);
    }

    @Test
    void isReportSubmissionExpired_report_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final Long facilityId = 1L;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final Year targetYear = Year.of(2018);
        final RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(targetYear)
                        .facility(FacilityBaseInfoDTO.builder().id(facilityId).build())
                        .build())
                .build();
        final LocalDate submissionDate = LocalDate.now();

        final TargetPeriodYear targetPeriodYear = TargetPeriodYear.builder().targetYear(targetYear).build();
        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .businessId(targetPeriodType)
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(targetPeriodYear))
                        .build())
                .build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(targetPeriod, reportType, submissionDate))
                .thenReturn(BusinessValidationResult.invalid(List.of()));
        when(performanceDataFacilityDigitalFormValidator.validateFacilityEligibility(facility, targetPeriodYear))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        boolean result = validator.isReportSubmissionExpired(requestTask, submissionDate);

        // Verify
        assertThat(result).isTrue();
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(targetPeriod, reportType, submissionDate);
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityEligibility(facility, targetPeriodYear);
    }

    @Test
    void validate() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final Long accountId = 1L;
        final String facilityBusinessId = "facilityBusinessId";
        final Year targetPeriodYear = Year.of(2018);
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .improvements(Map.of(TargetImprovementType.TP7, BigDecimal.ONE))
                .build();
        final PerformanceDataFacilityReferenceData referenceData = PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(baselineAndTargets)
                .tpMultiplier(BigDecimal.ONE)
                .build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
                        .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType(ResourceType.ACCOUNT)
                                .resourceId(accountId.toString())
                                .build())
                        )
                        .build())
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(targetPeriodYear)
                        .referenceData(referenceData)
                        .facility(FacilityBaseInfoDTO.builder().facilityBusinessId(facilityBusinessId).build())
                        .performanceData(performanceData)
                        .build())
                .build();
        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(TargetPeriodYear.builder().targetYear(Year.of(2026)).build()))
                                .build())
                        .build()
        );
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(targetPeriodType)
                .reportType(reportType)
                .targetYear(targetPeriodYear)
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.01))
                .improvements(Map.of(TargetImprovementType.TP7, BigDecimal.ONE))
                .lastYearPerTp(Map.of(TargetPeriodType.TP7, 2026))
                .build();

        when(performanceDataFacilityDigitalFormReferenceDataService
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear))
                .thenReturn(baselineAndTargets);
        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9)))
                .thenReturn(targetPeriods);
        when(performanceDataFacilityDigitalFormDataValidator.validateData(performanceData, calculationParameters))
                .thenReturn(List.of(BusinessValidationResult.valid()));
        when(performanceDataFacilityDigitalFormInputCalculatedDataValidator.validateInputCalculatedData(performanceData, calculationParameters))
                .thenReturn(List.of(BusinessValidationResult.valid()));
        when(performanceDataFacilityDigitalFormCalculatedDataValidator.validateCalculatedData(performanceData, calculationParameters))
                .thenReturn(List.of(BusinessValidationResult.valid()));

        // Invoke
        validator.validate(requestTask);

        // Verify
        verify(performanceDataFacilityDigitalFormReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear);
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
        verify(performanceDataFacilityDigitalFormDataValidator, times(1))
                .validateData(performanceData, calculationParameters);
        verify(performanceDataFacilityDigitalFormInputCalculatedDataValidator, times(1))
                .validateInputCalculatedData(performanceData, calculationParameters);
        verify(performanceDataFacilityDigitalFormCalculatedDataValidator, times(1))
                .validateCalculatedData(performanceData, calculationParameters);
    }

    @Test
    void validate_calculated_results_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final Long accountId = 1L;
        final String facilityBusinessId = "facilityBusinessId";
        final Year targetPeriodYear = Year.of(2018);
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .improvements(Map.of(TargetImprovementType.TP7, BigDecimal.ONE))
                .build();
        final PerformanceDataFacilityReferenceData referenceData = PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(baselineAndTargets)
                .tpMultiplier(BigDecimal.ONE)
                .build();
        final PerformanceDataFacilityCalculatedResults calculatedResults = PerformanceDataFacilityCalculatedResults.builder()
                .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .calculatedResults(calculatedResults)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType(ResourceType.ACCOUNT)
                                .resourceId(accountId.toString())
                                .build())
                        )
                        .build())
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(targetPeriodYear)
                        .referenceData(referenceData)
                        .facility(FacilityBaseInfoDTO.builder().facilityBusinessId(facilityBusinessId).build())
                        .performanceData(performanceData)
                        .build())
                .build();
        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(TargetPeriodYear.builder().targetYear(Year.of(2026)).build()))
                                .build())
                        .build()
        );
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(targetPeriodType)
                .reportType(reportType)
                .targetYear(targetPeriodYear)
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.01))
                .improvements(Map.of(TargetImprovementType.TP7, BigDecimal.ONE))
                .lastYearPerTp(Map.of(TargetPeriodType.TP7, 2026))
                .build();

        when(performanceDataFacilityDigitalFormReferenceDataService
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear))
                .thenReturn(baselineAndTargets);
        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9)))
                .thenReturn(targetPeriods);
        when(performanceDataFacilityDigitalFormDataValidator.validateData(performanceData, calculationParameters))
                .thenReturn(List.of(BusinessValidationResult.valid()));
        when(performanceDataFacilityDigitalFormInputCalculatedDataValidator.validateInputCalculatedData(performanceData, calculationParameters))
                .thenReturn(List.of(BusinessValidationResult.valid()));
        when(performanceDataFacilityDigitalFormCalculatedDataValidator.validateCalculatedData(performanceData, calculationParameters))
                .thenReturn(List.of(BusinessValidationResult.invalid(List.of())));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(requestTask));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CALCULATED_RESULTS);
        verify(performanceDataFacilityDigitalFormReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear);
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
        verify(performanceDataFacilityDigitalFormDataValidator, times(1))
                .validateData(performanceData, calculationParameters);
        verify(performanceDataFacilityDigitalFormInputCalculatedDataValidator, times(1))
                .validateInputCalculatedData(performanceData, calculationParameters);
        verify(performanceDataFacilityDigitalFormCalculatedDataValidator, times(1))
                .validateCalculatedData(performanceData, calculationParameters);
    }

    @Test
    void validate_input_calculated_results_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final Long accountId = 1L;
        final String facilityBusinessId = "facilityBusinessId";
        final Year targetPeriodYear = Year.of(2018);
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .improvements(Map.of(TargetImprovementType.TP7, BigDecimal.ONE))
                .build();
        final PerformanceDataFacilityReferenceData referenceData = PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(baselineAndTargets)
                .tpMultiplier(BigDecimal.ONE)
                .build();
        final PerformanceDataFacilityCalculatedResults calculatedResults = PerformanceDataFacilityCalculatedResults.builder()
                .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .calculatedResults(calculatedResults)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType(ResourceType.ACCOUNT)
                                .resourceId(accountId.toString())
                                .build())
                        )
                        .build())
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(targetPeriodYear)
                        .referenceData(referenceData)
                        .facility(FacilityBaseInfoDTO.builder().facilityBusinessId(facilityBusinessId).build())
                        .performanceData(performanceData)
                        .build())
                .build();
        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(TargetPeriodYear.builder().targetYear(Year.of(2026)).build()))
                                .build())
                        .build()
        );
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(targetPeriodType)
                .reportType(reportType)
                .targetYear(targetPeriodYear)
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.01))
                .improvements(Map.of(TargetImprovementType.TP7, BigDecimal.ONE))
                .lastYearPerTp(Map.of(TargetPeriodType.TP7, 2026))
                .build();

        when(performanceDataFacilityDigitalFormReferenceDataService
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear))
                .thenReturn(baselineAndTargets);
        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9)))
                .thenReturn(targetPeriods);
        when(performanceDataFacilityDigitalFormDataValidator.validateData(performanceData, calculationParameters))
                .thenReturn(List.of(BusinessValidationResult.valid()));
        when(performanceDataFacilityDigitalFormInputCalculatedDataValidator.validateInputCalculatedData(performanceData, calculationParameters))
                .thenReturn(List.of(BusinessValidationResult.invalid(List.of())));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(requestTask));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT);
        verify(performanceDataFacilityDigitalFormReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear);
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
        verify(performanceDataFacilityDigitalFormDataValidator, times(1))
                .validateData(performanceData, calculationParameters);
        verify(performanceDataFacilityDigitalFormInputCalculatedDataValidator, times(1))
                .validateInputCalculatedData(performanceData, calculationParameters);
        verifyNoInteractions(performanceDataFacilityDigitalFormCalculatedDataValidator);
    }

    @Test
    void validate_data_not_valid() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final Long accountId = 1L;
        final String facilityBusinessId = "facilityBusinessId";
        final Year targetPeriodYear = Year.of(2018);
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .improvements(Map.of(TargetImprovementType.TP7, BigDecimal.ONE))
                .build();
        final PerformanceDataFacilityReferenceData referenceData = PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(baselineAndTargets)
                .tpMultiplier(BigDecimal.ONE)
                .build();
        final PerformanceDataFacilityInputData performanceData = PerformanceDataFacilityInputData.builder()
                .calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
                        .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType(ResourceType.ACCOUNT)
                                .resourceId(accountId.toString())
                                .build())
                        )
                        .build())
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(targetPeriodYear)
                        .referenceData(referenceData)
                        .facility(FacilityBaseInfoDTO.builder().facilityBusinessId(facilityBusinessId).build())
                        .performanceData(performanceData)
                        .build())
                .build();
        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(TargetPeriodYear.builder().targetYear(Year.of(2026)).build()))
                                .build())
                        .build()
        );
        final PerformanceDataFacilityCalculationParameters calculationParameters = PerformanceDataFacilityCalculationParameters.builder()
                .targetPeriodType(targetPeriodType)
                .reportType(reportType)
                .targetYear(targetPeriodYear)
                .tpMultiplier(BigDecimal.ONE)
                .targetImprovement(BigDecimal.valueOf(0.01))
                .improvements(Map.of(TargetImprovementType.TP7, BigDecimal.ONE))
                .lastYearPerTp(Map.of(TargetPeriodType.TP7, 2026))
                .build();

        when(performanceDataFacilityDigitalFormReferenceDataService
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear))
                .thenReturn(baselineAndTargets);
        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9)))
                .thenReturn(targetPeriods);
        when(performanceDataFacilityDigitalFormDataValidator.validateData(performanceData, calculationParameters))
                .thenReturn(List.of(BusinessValidationResult.invalid(List.of())));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(requestTask));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT);
        verify(performanceDataFacilityDigitalFormReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear);
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
        verifyNoInteractions(performanceDataFacilityDigitalFormInputCalculatedDataValidator, performanceDataFacilityDigitalFormCalculatedDataValidator);
    }

    @Test
    void validate_reference_data_not_valid() {
        final Long accountId = 1L;
        final String facilityBusinessId = "facilityBusinessId";
        final Year targetPeriodYear = Year.of(2018);
        final PerformanceDataFacilityReferenceData referenceData = PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                        .improvements(Map.of(TargetImprovementType.TP7, BigDecimal.ONE))
                        .build())
                .tpMultiplier(BigDecimal.ONE)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType(ResourceType.ACCOUNT)
                                .resourceId(accountId.toString())
                                .build())
                        )
                        .build())
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodYear(targetPeriodYear)
                        .referenceData(referenceData)
                        .facility(FacilityBaseInfoDTO.builder().facilityBusinessId(facilityBusinessId).build())
                        .build())
                .build();

        when(performanceDataFacilityDigitalFormReferenceDataService
                .getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear))
                .thenReturn(PerformanceDataFacilityBaselineAndTargets.builder().measurementType(MeasurementType.ENERGY_MWH).build());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(requestTask));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_ORIGINAL_BASELINE_AND_TARGETS_IS_OUTDATED);
        verifyNoInteractions(targetPeriodService, performanceDataFacilityDigitalFormDataValidator, performanceDataFacilityDigitalFormInputCalculatedDataValidator,
                performanceDataFacilityDigitalFormCalculatedDataValidator);
    }
}
