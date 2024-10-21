package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform.UnderlyingAgreementContainerMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.UnderlyingAgreementTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementReviewValidatorService {

    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final UnderlyingAgreementTargetUnitDetailsValidatorService underlyingAgreementTargetUnitDetailsValidatorService;
    private final UnderlyingAgreementReviewNotifyOperatorValidator underlyingAgreementReviewNotifyOperatorValidator;
    private final UnderlyingAgreementReviewDeterminationDecisionsValidator determinationDecisionsValidator ;
    private static final UnderlyingAgreementContainerMapper UNDERLYING_AGREEMENT_CONTAINER_MAPPER = Mappers.getMapper(UnderlyingAgreementContainerMapper.class);

    public void validate(final RequestTask requestTask, final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload, AppUser appUser) {
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        UnderlyingAgreementContainer unaContainer = UNDERLYING_AGREEMENT_CONTAINER_MAPPER.toUnderlyingAgreementContainer(taskPayload);

        // Validate underlying agreement
        List<BusinessValidationResult> validationResults = underlyingAgreementValidatorService.getValidationResults(unaContainer);

        // Validate if Sector user's facilityIds match Regulator's
        validationResults.add(validateFacilityIds(taskPayload,
                ((UnderlyingAgreementRequestPayload) requestTask.getRequest().getPayload())));

        // Validate target unit
        validationResults.add(underlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask));

        // Validate form
        validationResults.add(determinationDecisionsValidator.validateOverallDecision(taskPayload));

        // Validate decision notification
        validationResults.addAll(underlyingAgreementReviewNotifyOperatorValidator.validate(requestTask, payload, appUser));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_REVIEW, ValidatorHelper.extractViolations(validationResults));
        }
    }

    private BusinessValidationResult validateFacilityIds(UnderlyingAgreementReviewRequestTaskPayload taskPayload,
                                              UnderlyingAgreementRequestPayload requestPayload) {
        Set<String> facilityIds = taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities().stream()
                .map(facility -> facility.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());
        Set<String> sectorUserFacilityIds = requestPayload.getFacilityIds();
        Set<String> diffs = SetUtils.disjunction(facilityIds, sectorUserFacilityIds);
        if(!diffs.isEmpty()) {
            return BusinessValidationResult.builder()
                    .valid(false)
                    .violations(List.of(
                            new UnderlyingAgreementViolation(Facility.class.getName(),
                                    UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_SECTION_DATA, diffs)
                    ))
                    .build();
        }

        return BusinessValidationResult.valid();
    }
}
