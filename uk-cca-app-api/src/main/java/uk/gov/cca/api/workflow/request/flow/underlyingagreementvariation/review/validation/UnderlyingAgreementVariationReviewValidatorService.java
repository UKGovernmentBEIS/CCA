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
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.transform.UnderlyingAgreementVariationContainerMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationApplicationReasonDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementPayloadType.EDITED;
import static uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementPayloadType.PROPOSED;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewValidatorService {

    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
    private final ProposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService proposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
    private final UnderlyingAgreementVariationReviewNotifyOperatorValidator underlyingAgreementVariationReviewNotifyOperatorValidator;
    private final EditedUnderlyingAgreementVariationDetailsValidatorService editedUnderlyingAgreementVariationDetailsValidatorService;
    private final ProposedUnderlyingAgreementVariationDetailsValidatorService proposedUnderlyingAgreementVariationDetailsValidatorService;
    private final EditedUnderlyingAgreementVariationApplicationReasonDataValidator editedUnderlyingAgreementVariationApplicationReasonDataValidator;
    private final ProposedUnderlyingAgreementVariationApplicationReasonDataValidator proposedUnderlyingAgreementVariationApplicationReasonDataValidator;

    private static final UnderlyingAgreementVariationContainerMapper UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER =
            Mappers.getMapper(UnderlyingAgreementVariationContainerMapper.class);

    public void validate(final RequestTask requestTask, final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload, AppUser appUser) {

        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        UnderlyingAgreementContainer editedUnaContainer = UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER.toUnderlyingAgreementContainer(taskPayload);

        UnderlyingAgreementContainer proposedUnaContainer =
                UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER.toUnderlyingAgreementProposedContainer(taskPayload);

        // Validate underlying agreement
        List<BusinessValidationResult> validationResults = this.updateViolationMsgWithSectionInfo(underlyingAgreementValidatorService.getValidationResults(editedUnaContainer), EDITED);
        validationResults.addAll(this.updateViolationMsgWithSectionInfo(underlyingAgreementValidatorService.getValidationResults(proposedUnaContainer), PROPOSED));

        UnderlyingAgreementVariationRequestPayload requestPayload =
                (UnderlyingAgreementVariationRequestPayload) requestTask.getRequest().getPayload();

        // Validate if Sector user's facilityIds match Regulator's
        validationResults.add(validateEditedFacilityIds(taskPayload, requestPayload));
        validationResults.add(validateProposedFacilityIds(taskPayload, requestPayload));

        // Validate target unit
        validationResults.add(editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask));
        validationResults.add(proposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask));

        // Validate variation details
        validationResults.add(editedUnderlyingAgreementVariationDetailsValidatorService.validate(taskPayload));
        validationResults.add(proposedUnderlyingAgreementVariationDetailsValidatorService.validate(taskPayload));

        // Validate previous facility ids and application reasons
        validationResults.add(editedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload));
        validationResults.add(proposedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload));

        // Validate decision notification
        validationResults.addAll(underlyingAgreementVariationReviewNotifyOperatorValidator.validate(requestTask, payload, appUser));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION_REVIEW, ValidatorHelper.extractViolations(validationResults));
        }
    }

    private BusinessValidationResult validateEditedFacilityIds(UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload,
                                                               UnderlyingAgreementVariationRequestPayload requestPayload) {
        Set<String> facilityIds = taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities().stream()
                .map(facility -> facility.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());
        Set<String> sectorUserFacilityIds = requestPayload.getFacilityIds();
        Set<String> diffs = SetUtils.disjunction(facilityIds, sectorUserFacilityIds);
        if (!diffs.isEmpty()) {
            return BusinessValidationResult.builder()
                    .valid(false)
                    .violations(List.of(
                            new UnderlyingAgreementVariationViolation(EDITED + Facility.class.getName(),
                                    UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_SECTION_DATA, diffs)
                    ))
                    .build();
        }

        return BusinessValidationResult.valid();
    }

    private BusinessValidationResult validateProposedFacilityIds(UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload,
                                                                 UnderlyingAgreementVariationRequestPayload requestPayload) {
        Set<String> facilityIds = taskPayload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities().stream()
                .map(facility -> facility.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());
        Set<String> sectorUserFacilityIds = requestPayload.getFacilityIds();

        // Proposed UnA facilities should be a subset of request facilities
        Set<String> diffs = SetUtils.difference(facilityIds, sectorUserFacilityIds);
        if (!diffs.isEmpty()) {
            return BusinessValidationResult.builder()
                    .valid(false)
                    .violations(List.of(
                            new UnderlyingAgreementVariationViolation(PROPOSED + Facility.class.getName(),
                                    UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_SECTION_DATA, diffs)
                    ))
                    .build();
        }

        return BusinessValidationResult.valid();
    }

    private List<BusinessValidationResult> updateViolationMsgWithSectionInfo(
            final List<BusinessValidationResult> validationResults,
            final UnderlyingAgreementPayloadType payloadType) {
        validationResults.forEach(result ->
                result.getViolations().forEach(v -> v.setSectionName(payloadType + v.getSectionName())));
        return validationResults;
    }
}
