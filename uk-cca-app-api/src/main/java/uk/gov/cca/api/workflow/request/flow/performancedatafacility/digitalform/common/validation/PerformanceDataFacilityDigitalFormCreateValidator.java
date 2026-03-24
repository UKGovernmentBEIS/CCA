package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByFacilityValidator;
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

    private final TargetPeriodService targetPeriodService;
    private final RequestQueryService requestQueryService;
    private final PerformanceDataFacilityDigitalFormValidator performanceDataFacilityDigitalFormValidator;

    @Override
    public RequestCreateValidationResult validateAction(Long facilityId) {
        final TargetPeriodDetailsDTO tp7TargetPeriod = targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType.TP7);

        // Digital forms only valid for TP7 and after
        if(LocalDate.now().isBefore(tp7TargetPeriod.getTargetPeriodYearsContainer().getTargetPeriodReportingStartDate())) {
            return RequestCreateValidationResult.builder().valid(true).isAvailable(false).build();
        }

        return RequestCreateValidationResult.builder().valid(true).isAvailable(true).build();
    }

    @Override
    public RequestCreateValidationResult validateAction(Long facilityId, PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload) {
        final LocalDate submissionDate = LocalDate.now();

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
        BusinessValidationResult validationReportTypeResult = performanceDataFacilityDigitalFormValidator
                .validateReportSubmission(payload.getTargetPeriodType(), payload.getReportType(), submissionDate);
        if(!validationReportTypeResult.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REPORT_NOT_ELIGIBLE, validationReportTypeResult.getViolations());
        }

        // Check facility
        BusinessValidationResult validationFacilityResult = performanceDataFacilityDigitalFormValidator
                .validateFacilityEligibility(facilityId, payload.getTargetPeriodType(), submissionDate);
        if(!validationFacilityResult.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_NOT_ELIGIBLE, validationFacilityResult.getViolations());
        }

        return RequestCreateValidationResult.builder().valid(true).isAvailable(true).build();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM;
    }
}
