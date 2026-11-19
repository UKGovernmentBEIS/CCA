package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByFacilityValidator;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.config.EmissionsTradingSchemeDataConfig;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestCreateActionPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class EmissionsTradingSchemeDataCreateValidator implements RequestCreateByFacilityValidator, RequestCreateByRequestValidator<EmissionsTradingSchemeDataRequestCreateActionPayload> {

    private final TargetPeriodService targetPeriodService;
    private final EmissionsTradingSchemeDataConfig emissionsTradingSchemeDataConfig;
    private final FacilityDataQueryService facilityDataQueryService;
    private final CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Override
    public RequestCreateValidationResult validateAction(Long facilityId) {
        LocalDate submissionDate = Optional.ofNullable(emissionsTradingSchemeDataConfig.getSubmissionDate())
                .orElse(LocalDate.now());

        boolean isSubmissionDateValid = isSubmissionDateValid(submissionDate);

        if (!isSubmissionDateValid) {
            return RequestCreateValidationResult.builder().valid(true).available(false).build();
        }

        // Facility only available for CCA3 participating Scheme
        final FacilityDTO facility = facilityDataQueryService.getFacilityInfoData(facilityId);
        if (!facility.getParticipatingSchemeVersions().contains(SchemeVersion.CCA_3)) {
            return RequestCreateValidationResult.builder().valid(true).available(false).build();
        }

        // Check no request in progress
        return ccaRequestCreateValidatorService
                .validate(facilityId, CcaResourceType.FACILITY, Set.of(CcaRequestType.EMISSIONS_TRADING_SCHEME_DATA));
    }

    @Override
    public RequestCreateValidationResult validateAction(Long facilityId, EmissionsTradingSchemeDataRequestCreateActionPayload payload) {
        Year targetYear = payload.getTargetYear();

        // Check that target year is in active target period
        if (!isInActiveTargetPeriod(targetYear)) {
            throw new BusinessException(CcaErrorCode.EMISSIONS_TRADING_SCHEME_DATA_TARGET_YEAR_NOT_ELIGIBLE);
        }

        // Check submission date valid
        LocalDate submissionDate = Optional.ofNullable(emissionsTradingSchemeDataConfig.getSubmissionDate())
                .orElse(LocalDate.now());
        if (!isSubmissionDateValid(targetYear, submissionDate)) {
            throw new BusinessException(CcaErrorCode.EMISSIONS_TRADING_SCHEME_DATA_SUBMISSION_DATE_NOT_VALID);
        }

        // Check facility eligibility
        FacilityDTO facilityInfoData = facilityDataQueryService.getFacilityInfoData(facilityId);
        if (!isFacilityEligible(facilityInfoData, targetYear)) {
            throw new BusinessException(CcaErrorCode.EMISSIONS_TRADING_SCHEME_DATA_FACILITY_NOT_ELIGIBLE);
        }

        return RequestCreateValidationResult.builder().valid(true).available(true).build();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.EMISSIONS_TRADING_SCHEME_DATA;
    }

    private boolean isFacilityEligible(FacilityDTO facility, Year targetYear) {
        // facility_created_date <= target_year_end_date && facility_closed_date > target_year_end_date
        LocalDate targetYearEndDate = targetYear.atMonth(12).atEndOfMonth();
        return !facility.getCreatedDate().toLocalDate().isAfter(targetYearEndDate)
                && Optional.ofNullable(facility.getClosedDate())
                .map(closeDate -> closeDate.isAfter(targetYearEndDate))
                .orElse(true);
    }

    private boolean isSubmissionDateValid(LocalDate submissionDate) {
        Year reportingYear = Year.of(submissionDate.getYear());
        return reportingYear.compareTo(getMinimumEligibleReportingYear()) >= 0;
    }

    private boolean isSubmissionDateValid(Year targetYear, LocalDate submissionDate) {
        LocalDate reportingPeriodStartDate = LocalDate.of(targetYear.plusYears(1).getValue(), 1, 1);
        return !submissionDate.isBefore(LocalDate.now()) && !submissionDate.isBefore(reportingPeriodStartDate);
    }

    private boolean isInActiveTargetPeriod(Year targetYear) {
        return getActiveTargetPeriodYears().anyMatch(targetYear::equals);
    }

    private Stream<Year> getActiveTargetPeriodYears() {
        List<TargetPeriodDetailsDTO> activeTargetPeriods = targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(SchemeVersion.CCA_3);
        return activeTargetPeriods.stream()
                .flatMap(tp -> tp.getTargetPeriodYearsContainer().getTargetPeriodYears().stream())
                .map(TargetPeriodYear::getTargetYear);
    }

    private Year getMinimumEligibleReportingYear() {
        return getActiveTargetPeriodYears()
                .map(y -> y.plusYears(1))
                .min(Year::compareTo)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
