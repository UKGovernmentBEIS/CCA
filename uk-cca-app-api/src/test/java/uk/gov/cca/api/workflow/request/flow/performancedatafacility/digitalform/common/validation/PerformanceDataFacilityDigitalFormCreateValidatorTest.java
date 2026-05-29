package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.validation.performancedata.PerformanceDataCreateSchemeValidator;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestMetadata;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.List;
import java.util.Set;

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
    private PerformanceDataCreateSchemeValidator performanceDataCreateSchemeValidator;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private PerformanceDataFacilityDigitalFormValidator performanceDataFacilityDigitalFormValidator;

    @Test
    void validateAction() {
        final Long facilityId = 1L;

        final FacilityDTO facility = FacilityDTO.builder()
                .id(facilityId)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                .build();

        when(performanceDataCreateSchemeValidator.isAvailableForScheme(eq(SchemeVersion.CCA_3), any()))
                .thenReturn(true);
        when(facilityDataQueryService.getFacilityInfoData(facilityId))
                .thenReturn(facility);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(facilityId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(performanceDataCreateSchemeValidator, times(1))
                .isAvailableForScheme(eq(SchemeVersion.CCA_3), any());
        verify(facilityDataQueryService, times(1))
                .getFacilityInfoData(facilityId);
    }

    @Test
    void validateAction_facility_scheme_not_valid() {
        final Long facilityId = 1L;

        final FacilityDTO facility = FacilityDTO.builder()
                .id(facilityId)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .build();

        when(performanceDataCreateSchemeValidator.isAvailableForScheme(eq(SchemeVersion.CCA_3), any()))
                .thenReturn(true);
        when(facilityDataQueryService.getFacilityInfoData(facilityId))
                .thenReturn(facility);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(facilityId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).isAvailable(false).build());
        verify(performanceDataCreateSchemeValidator, times(1))
                .isAvailableForScheme(eq(SchemeVersion.CCA_3), any());
        verify(facilityDataQueryService, times(1))
                .getFacilityInfoData(facilityId);
    }

    @Test
    void validateAction_not_valid() {
        final Long facilityId = 1L;

        when(performanceDataCreateSchemeValidator.isAvailableForScheme(eq(SchemeVersion.CCA_3), any()))
                .thenReturn(false);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(facilityId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).isAvailable(false).build());
        verify(performanceDataCreateSchemeValidator, times(1))
                .isAvailableForScheme(eq(SchemeVersion.CCA_3), any());
        verifyNoInteractions(facilityDataQueryService);
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

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString())).thenReturn(List.of());
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(eq(targetPeriod), eq(reportType), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityReportingLock(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityEligibility(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityBaselineDateEligibility(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityProductsEligibility(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(facilityId, payload);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(eq(targetPeriod), eq(reportType), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityReportingLock(eq(facility), eq(targetPeriod), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityEligibility(eq(facility), eq(targetPeriod), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityBaselineDateEligibility(eq(facility), eq(targetPeriod), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityProductsEligibility(eq(facility), eq(targetPeriod), any());
    }

    @Test
    void validateAction_with_payload_facility_products_not_valid() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload =
                PerformanceDataFacilityDigitalFormRequestCreateActionPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .build();

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString())).thenReturn(List.of());
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(eq(targetPeriod), eq(reportType), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityReportingLock(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityEligibility(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityBaselineDateEligibility(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityProductsEligibility(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.invalid(List.of(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE_PRODUCTS))));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_PRODUCTS_NOT_ELIGIBLE);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(eq(targetPeriod), eq(reportType), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityReportingLock(eq(facility), eq(targetPeriod), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityEligibility(eq(facility), eq(targetPeriod), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityBaselineDateEligibility(eq(facility), eq(targetPeriod), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityProductsEligibility(eq(facility), eq(targetPeriod), any());
    }

    @Test
    void validateAction_with_payload_facility_baseline_date_not_valid() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload =
                PerformanceDataFacilityDigitalFormRequestCreateActionPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .build();

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString())).thenReturn(List.of());
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(eq(targetPeriod), eq(reportType), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityReportingLock(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityEligibility(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityBaselineDateEligibility(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.invalid(List.of(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_BASELINE_DATE_NOT_ELIGIBLE))));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_BASELINE_DATE_NOT_ELIGIBLE);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(eq(targetPeriod), eq(reportType), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityReportingLock(eq(facility), eq(targetPeriod), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityEligibility(eq(facility), eq(targetPeriod), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityBaselineDateEligibility(eq(facility), eq(targetPeriod), any());
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

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString())).thenReturn(List.of());
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(eq(targetPeriod), eq(reportType), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityReportingLock(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityEligibility(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.invalid(List.of(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_NOT_ELIGIBLE))));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_NOT_ELIGIBLE);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(eq(targetPeriod), eq(reportType), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityReportingLock(eq(facility), eq(targetPeriod), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityEligibility(eq(facility), eq(targetPeriod), any());
        verifyNoMoreInteractions(performanceDataFacilityDigitalFormValidator);
    }

    @Test
    void validateAction_with_payload_facility_lock_not_valid() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload =
                PerformanceDataFacilityDigitalFormRequestCreateActionPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .build();

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString())).thenReturn(List.of());
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(eq(targetPeriod), eq(reportType), any()))
                .thenReturn(BusinessValidationResult.valid());
        when(performanceDataFacilityDigitalFormValidator.validateFacilityReportingLock(eq(facility), eq(targetPeriod), any()))
                .thenReturn(BusinessValidationResult.invalid(List.of(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.FACILITY_IS_LOCKED))));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SECONDARY_REPORTING_LOCKED);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(eq(targetPeriod), eq(reportType), any());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateFacilityReportingLock(eq(facility), eq(targetPeriod), any());
        verifyNoMoreInteractions(performanceDataFacilityDigitalFormValidator);
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

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString())).thenReturn(List.of());
        when(performanceDataFacilityDigitalFormValidator.validateReportSubmission(eq(targetPeriod), eq(reportType), any()))
                .thenReturn(BusinessValidationResult.invalid(List.of()));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REPORT_NOT_ELIGIBLE);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verify(performanceDataFacilityDigitalFormValidator, times(1))
                .validateReportSubmission(eq(targetPeriod), eq(reportType), any());
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

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder().businessId(targetPeriodType).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString()))
                .thenReturn(List.of(request));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_EXIST);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(requestQueryService, times(1)).findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, facilityId.toString());
        verifyNoInteractions(performanceDataFacilityDigitalFormValidator);
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType()).isEqualTo(CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM);
    }
}
