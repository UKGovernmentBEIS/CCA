package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestMetadata;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormCreateValidatorTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormCreateValidator validator;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private PerformanceDataFacilityDigitalFormValidator performanceDataFacilityDigitalFormValidator;

    @Test
    void validateAction() {
        final LocalDate reportingStartDate = LocalDate.of(2018,1,1);

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder().reportingStartDate(reportingStartDate).build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType.TP7))
                .thenReturn(targetPeriodDetails);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(1L);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType.TP7);
    }

    @Test
    void validateAction_not_valid() {
        final LocalDate reportingStartDate = LocalDate.of(2118,1,1);

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder().reportingStartDate(reportingStartDate).build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType.TP7))
                .thenReturn(targetPeriodDetails);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(1L);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).isAvailable(false).build());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType.TP7);
    }

    @Test
    void validateAction_with_payload() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload =
                PerformanceDataFacilityDigitalFormRequestCreateActionPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .build();

        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString())).thenReturn(List.of());
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(eq(targetPeriodType), eq(reportType), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityEligibility(eq(facilityId), eq(targetPeriodType), any()))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(facilityId, payload);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(eq(targetPeriodType), eq(reportType), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityEligibility(eq(facilityId), eq(targetPeriodType), any());
    }

    @Test
    void validateAction_with_payload_facility_not_valid() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload =
                PerformanceDataFacilityDigitalFormRequestCreateActionPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .build();

        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString())).thenReturn(List.of());
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(eq(targetPeriodType), eq(reportType), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityEligibility(eq(facilityId), eq(targetPeriodType), any()))
                .thenReturn(BusinessValidationResult.invalid(List.of()));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_NOT_ELIGIBLE);
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(eq(targetPeriodType), eq(reportType), any());
    }

    @Test
    void validateAction_with_payload_report_type_not_valid() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload =
                PerformanceDataFacilityDigitalFormRequestCreateActionPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .build();

        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString())).thenReturn(List.of());
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(eq(targetPeriodType), eq(reportType), any()))
                .thenReturn(BusinessValidationResult.invalid(List.of()));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REPORT_NOT_ELIGIBLE);
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(eq(targetPeriodType), eq(reportType), any());
        verifyNoMoreInteractions(performanceDataFacilityDigitalFormValidator);
    }

    @Test
    void validateAction_with_payload_in_progress_not_valid() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload =
                PerformanceDataFacilityDigitalFormRequestCreateActionPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .build();

        final Request request = Request.builder()
                .status(RequestStatuses.IN_PROGRESS)
                .metadata(PerformanceDataFacilityDigitalFormRequestMetadata.builder()
                        .targetPeriodType(targetPeriodType)
                        .build())
                .build();

        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString()))
                .thenReturn(List.of(request));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_EXIST);
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verifyNoInteractions(performanceDataFacilityDigitalFormValidator);
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType()).isEqualTo(CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM);
    }
}
