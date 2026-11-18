package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityValidator;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation.PerformanceDataFacilityInputDataValidator;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.transform.PerformanceDataFacilityDigitalFormSubmitMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormSubmitValidator {

    private final TargetPeriodService targetPeriodService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final PerformanceDataFacilityValidator performanceDataFacilityValidator;
    private final PerformanceDataFacilityReferenceDataService performanceDataFacilityDigitalFormReferenceDataService;
    private final PerformanceDataFacilityInputDataValidator performanceDataFacilityDigitalFormDataValidator;
    private final PerformanceDataFacilityDigitalFormInputCalculatedDataValidator performanceDataFacilityDigitalFormInputCalculatedDataValidator;
    private final PerformanceDataFacilityDigitalFormCalculatedDataValidator performanceDataFacilityDigitalFormCalculatedDataValidator;

    private static final PerformanceDataFacilityDigitalFormSubmitMapper MAPPER = Mappers
            .getMapper(PerformanceDataFacilityDigitalFormSubmitMapper.class);

    public boolean isReportSubmissionExpired(final RequestTask requestTask, LocalDate submissionDate) {
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();
        final TargetPeriodDetailsDTO targetPeriod = targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(taskPayload.getTargetPeriodType());

        BusinessValidationResult validationReportTypeResult = performanceDataFacilityValidator
                .validateReportSubmission(targetPeriod, taskPayload.getReportType(), submissionDate);

        return !validationReportTypeResult.isValid();
    }

    public void validate(final RequestTask requestTask) {
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate Reference data
        validateOriginalBaselineAndTargets(requestTask);

        final List<TargetPeriodDetailsDTO> targetPeriods = targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(
                Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));

        // Validate Facility eligibility
        validateFacilityEligibility(taskPayload, targetPeriods);

        final PerformanceDataFacilityCalculationParameters calculationParameters = MAPPER
                .toPerformanceDataFacilityCalculationParameters(taskPayload, targetPeriods);

        // Validate data
        List<BusinessValidationResult> validationDataResults = performanceDataFacilityDigitalFormDataValidator
                .validateData(taskPayload.getPerformanceData(), calculationParameters);

        boolean isValid = validationDataResults.stream().allMatch(BusinessValidationResult::isValid);
        if(!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT, ValidatorHelper.extractViolations(validationDataResults));
        }

        // Validate Input Calculated data
        List<BusinessValidationResult> validationInputCalculatedResults = performanceDataFacilityDigitalFormInputCalculatedDataValidator
                .validateInputCalculatedData(taskPayload.getPerformanceData(), calculationParameters);

        boolean isInputCalculationValid = validationInputCalculatedResults.stream().allMatch(BusinessValidationResult::isValid);
        if(!isInputCalculationValid) {
            throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT, ValidatorHelper.extractViolations(validationInputCalculatedResults));
        }

        // Validate Calculated results
        List<BusinessValidationResult> validationCalculatedResults = performanceDataFacilityDigitalFormCalculatedDataValidator
                .validateCalculatedData(taskPayload.getPerformanceData(), calculationParameters);

        boolean isCalculationValid = validationCalculatedResults.stream().allMatch(BusinessValidationResult::isValid);
        if(!isCalculationValid) {
            throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CALCULATED_RESULTS, ValidatorHelper.extractViolations(validationCalculatedResults));
        }
    }

    public void validateFacilityEligibility(final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload, final List<TargetPeriodDetailsDTO> targetPeriods) {
        final TargetPeriodDetailsDTO targetPeriod = targetPeriods.stream()
                .filter(tp -> tp.getBusinessId().equals((taskPayload.getTargetPeriodType())))
                .findFirst().orElse(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(taskPayload.getTargetPeriodType()));
        final FacilityDTO facility = facilityDataQueryService.getFacilityInfoData(taskPayload.getFacility().getId());
        final TargetPeriodYear targetPeriodYear = targetPeriod.getTargetPeriodYearsContainer()
                .getTargetPeriodYear(taskPayload.getTargetPeriodYear())
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));

        BusinessValidationResult validationLockingResults = performanceDataFacilityValidator
                .validateFacilityReportingLock(facility, targetPeriodYear);

        if(!validationLockingResults.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_REPORTING_LOCKED);
        }

        BusinessValidationResult validationFacilityResults = performanceDataFacilityValidator
                .validateFacilityEligibility(facility, targetPeriodYear);

        if(!validationFacilityResults.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_NOT_ELIGIBLE);
        }

        BusinessValidationResult validationFacilityBaselineDateResults = performanceDataFacilityValidator
                .validateFacilityBaselineDateEligibility(facility, targetPeriodYear);

        if(!validationFacilityBaselineDateResults.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_BASELINE_DATE_NOT_ELIGIBLE);
        }

        BusinessValidationResult validationFacilityProductsResults = performanceDataFacilityValidator
                .validateFacilityProductsEligibility(facility, targetPeriodYear);

        if(!validationFacilityProductsResults.isValid()) {
            throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_FACILITY_PRODUCTS_NOT_ELIGIBLE);
        }
    }

    private void validateOriginalBaselineAndTargets(final RequestTask requestTask) {
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final PerformanceDataFacilityBaselineAndTargets originalBaselineTargets = taskPayload.getReferenceData().getBaselineAndTargets();
        final PerformanceDataFacilityBaselineAndTargets persistedBaselineTargets = performanceDataFacilityDigitalFormReferenceDataService
                .getFacilityOriginalBaselineAndTargets(request.getAccountId(), taskPayload.getFacility().getFacilityBusinessId(), taskPayload.getTargetPeriodYear());

        // If baseline data differ throw refresh exception
        if(!originalBaselineTargets.equals(persistedBaselineTargets)) {
            throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_ORIGINAL_BASELINE_AND_TARGETS_IS_OUTDATED);
        }
    }
}
