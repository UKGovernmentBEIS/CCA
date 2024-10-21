package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.validation;

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
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.transform.UnderlyingAgreementVariationContainerMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewValidatorService {

    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final UnderlyingAgreementVariationTargetUnitDetailsValidatorService underlyingAgreementVariationTargetUnitDetailsValidatorService;
    private final UnderlyingAgreementVariationReviewNotifyOperatorValidator underlyingAgreementVariationReviewNotifyOperatorValidator;
    private final UnderlyingAgreementVariationReviewDeterminationDecisionsValidator underlyingAgreementVariationReviewDeterminationDecisionsValidator ;
    private final UnderlyingAgreementVariationDetailsValidatorService underlyingAgreementVariationDetailsValidatorService;
    private static final UnderlyingAgreementVariationContainerMapper UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER =
            Mappers.getMapper(UnderlyingAgreementVariationContainerMapper.class);

    public void validate(final RequestTask requestTask, final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload, AppUser appUser) {
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        UnderlyingAgreementContainer unaContainer = UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER.toUnderlyingAgreementContainer(taskPayload);

        // Validate underlying agreement
        List<BusinessValidationResult> validationResults = underlyingAgreementValidatorService.getValidationResults(unaContainer);

        // Validate if Sector user's facilityIds match Regulator's
        validationResults.add(validateFacilityIds(taskPayload,
                ((UnderlyingAgreementVariationRequestPayload) requestTask.getRequest().getPayload())));

        // Validate target unit
        validationResults.add(underlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask));

        // Validate variation details
        validationResults.add(underlyingAgreementVariationDetailsValidatorService.validate(requestTask));

        // Validate form
        validationResults.add(underlyingAgreementVariationReviewDeterminationDecisionsValidator.validateOverallDecision(taskPayload));

        // Validate decision notification
        validationResults.addAll(underlyingAgreementVariationReviewNotifyOperatorValidator.validate(requestTask, payload, appUser));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION_REVIEW, ValidatorHelper.extractViolations(validationResults));
        }
    }

    private BusinessValidationResult validateFacilityIds(UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload,
                                                         UnderlyingAgreementVariationRequestPayload requestPayload) {
        Set<String> facilityIds = taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities().stream()
                .map(facility -> facility.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());
        Set<String> sectorUserFacilityIds = requestPayload.getFacilityIds();
        Set<String> diffs = SetUtils.disjunction(facilityIds, sectorUserFacilityIds);
        if(!diffs.isEmpty()) {
            return BusinessValidationResult.builder()
                    .valid(false)
                    .violations(List.of(
                            new UnderlyingAgreementVariationViolation(Facility.class.getName(),
                                    UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_SECTION_DATA, diffs)
                    ))
                    .build();
        }

        return BusinessValidationResult.valid();
    }
}
