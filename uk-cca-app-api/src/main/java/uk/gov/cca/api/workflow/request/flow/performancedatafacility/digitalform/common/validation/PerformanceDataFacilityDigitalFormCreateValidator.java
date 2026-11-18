package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityValidator;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByFacilityValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.performancedata.PerformanceDataCreateSchemeValidator;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestMetadata;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormCreateValidator implements RequestCreateByFacilityValidator, RequestCreateByRequestValidator<PerformanceDataFacilityDigitalFormRequestCreateActionPayload> {

    private final PerformanceDataCreateSchemeValidator performanceDataCreateSchemeValidator;
    private final TargetPeriodService targetPeriodService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final RequestQueryService requestQueryService;
    private final PerformanceDataFacilityValidator performanceDataFacilityValidator;

    @Override
    public RequestCreateValidationResult validateAction(Long facilityId) {
        // Digital forms only available for CCA3 Scheme
        if(!performanceDataCreateSchemeValidator.isAvailableForScheme(SchemeVersion.CCA_3, LocalDate.now())) {
            return RequestCreateValidationResult.builder().valid(true).isAvailable(false).build();
        }

        // Facility only available for CCA3 participating Scheme
        final FacilityDTO facility = facilityDataQueryService.getFacilityInfoData(facilityId);
        if(!facility.getParticipatingSchemeVersions().contains(SchemeVersion.CCA_3)) {
            return RequestCreateValidationResult.builder().valid(true).isAvailable(false).build();
        }

        return RequestCreateValidationResult.builder().valid(true).isAvailable(true).build();
    }

    @Override
    public RequestCreateValidationResult validateAction(Long facilityId, PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload) {
        final LocalDate submissionDate = LocalDate.now();
        final TargetPeriodDetailsDTO targetPeriod = targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(payload.getTargetPeriodType());
        final FacilityDTO facility = facilityDataQueryService.getFacilityInfoData(facilityId);

        // Check in progress
        List<Request> result = requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
                CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaResourceType.FACILITY, String.valueOf(facilityId));
        boolean inProgress = result.stream()
                .filter(request -> request.getStatus().equals(RequestStatuses.IN_PROGRESS))
                .map(request -> (PerformanceDataFacilityDigitalFormRequestMetadata) request.getMetadata())
                .anyMatch(metadata -> metadata.getTargetPeriodType().equals(payload.getTargetPeriodType()));
        if(inProgress) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_EXIST);
        }

        // Check target period and report type
        BusinessValidationResult validationReportTypeResult = performanceDataFacilityValidator
                .validateReportSubmission(targetPeriod, payload.getReportType(), submissionDate);
        if(!validationReportTypeResult.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REPORT_NOT_ELIGIBLE, validationReportTypeResult.getViolations());
        }

        // Check facility reporting lock status
        BusinessValidationResult validationSecondaryLockResult = performanceDataFacilityValidator
                .validateFacilityReportingLock(facility, targetPeriod, submissionDate);
        if(!validationSecondaryLockResult.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_REPORTING_LOCKED, validationSecondaryLockResult.getViolations());
        }

        // Check facility eligibility
        BusinessValidationResult validationFacilityResult = performanceDataFacilityValidator
                .validateFacilityEligibility(facility, targetPeriod, submissionDate);
        if(!validationFacilityResult.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_NOT_ELIGIBLE, validationFacilityResult.getViolations());
        }

        // Check facility baseline date eligibility
        BusinessValidationResult validationFacilityBaselineDateResult = performanceDataFacilityValidator
                .validateFacilityBaselineDateEligibility(facility, targetPeriod, submissionDate);
        if(!validationFacilityBaselineDateResult.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_BASELINE_DATE_NOT_ELIGIBLE, validationFacilityBaselineDateResult.getViolations());
        }

        // Check facility products
        BusinessValidationResult validationFacilityProductsResult = performanceDataFacilityValidator
                .validateFacilityProductsEligibility(facility, targetPeriod, submissionDate);
        if(!validationFacilityProductsResult.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_PRODUCTS_NOT_ELIGIBLE, validationFacilityProductsResult.getViolations());
        }

        return RequestCreateValidationResult.builder().valid(true).isAvailable(true).build();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM;
    }
}
