package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.TargetPeriodDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.transform.UnderlyingAgreementVariationContainerMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationApplicationReasonDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.transform.UnderlyingAgreementVariationReviewMapper;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementPayloadType.EDITED;
import static uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementPayloadType.PROPOSED;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewValidatorService {

    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final TargetPeriodDetailsValidatorService targetPeriodDetailsValidatorService;
    private final EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
    private final ProposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService proposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
    private final EditedUnderlyingAgreementVariationDetailsValidatorService editedUnderlyingAgreementVariationDetailsValidatorService;
    private final ProposedUnderlyingAgreementVariationDetailsValidatorService proposedUnderlyingAgreementVariationDetailsValidatorService;
    private final EditedUnderlyingAgreementVariationApplicationReasonDataValidator editedUnderlyingAgreementVariationApplicationReasonDataValidator;
    private final ProposedUnderlyingAgreementVariationApplicationReasonDataValidator proposedUnderlyingAgreementVariationApplicationReasonDataValidator;

    private static final UnderlyingAgreementVariationContainerMapper UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER =
            Mappers.getMapper(UnderlyingAgreementVariationContainerMapper.class);
    private static final UnderlyingAgreementVariationReviewMapper UNDERLYING_AGREEMENT_VARIATION_REVIEW_MAPPER =
            Mappers.getMapper(UnderlyingAgreementVariationReviewMapper.class);

    public List<BusinessValidationResult> validateEditedUnderlyingAgreement(final RequestTask requestTask) {
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(requestTask.getRequest().getCreationDate())
                .schemeVersion(taskPayload.getWorkflowSchemeVersion())
                .build();
        final UnderlyingAgreementContainer editedUnaContainer = UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER.toUnderlyingAgreementContainer(taskPayload);

        // Validate underlying agreement
        List<BusinessValidationResult> validationResults = this.updateViolationMsgWithSectionInfo(
                underlyingAgreementValidatorService.getValidationResults(editedUnaContainer, underlyingAgreementValidationContext), EDITED);

        // Validate target period details
        validationResults.add(this.updateViolationMsgWithSectionInfo(
                targetPeriodDetailsValidatorService.validate(editedUnaContainer, taskPayload.getOriginalUnderlyingAgreementContainer()),
                EDITED
        ));

        UnderlyingAgreementVariationRequestPayload requestPayload =
                (UnderlyingAgreementVariationRequestPayload) requestTask.getRequest().getPayload();

        // Validate if Sector user's facilityIds match Regulator
        validationResults.add(validateEditedFacilityIds(taskPayload, requestPayload));

        // Validate target unit
        validationResults.add(editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask));

        // Validate variation details
        validationResults.add(editedUnderlyingAgreementVariationDetailsValidatorService.validate(taskPayload));

        // Validate previous facility ids and application reasons
        validationResults.add(editedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload));

        return validationResults;
    }


    public List<BusinessValidationResult> validateProposedUnderlyingAgreement(final RequestTask requestTask) {
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(requestTask.getRequest().getCreationDate())
                .schemeVersion(taskPayload.getWorkflowSchemeVersion())
                .build();
        UnderlyingAgreementContainer proposedUnaContainer = UNDERLYING_AGREEMENT_VARIATION_REVIEW_MAPPER.toUnderlyingAgreementProposedContainer(taskPayload);

        // Validate underlying agreement
        List<BusinessValidationResult> validationResults = this.updateViolationMsgWithSectionInfo(
                underlyingAgreementValidatorService.getValidationResults(proposedUnaContainer, underlyingAgreementValidationContext), PROPOSED);

        // Validate target period details
        validationResults.add(this.updateViolationMsgWithSectionInfo(
                targetPeriodDetailsValidatorService.validate(proposedUnaContainer, taskPayload.getOriginalUnderlyingAgreementContainer()),
                PROPOSED
        ));

        UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) requestTask.getRequest().getPayload();

        // Validate if Sector user's facilityIds match Regulator
        validationResults.add(validateProposedFacilityIds(taskPayload, requestPayload));

        // Validate target unit
        validationResults.add(proposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask));

        // Validate variation details
        validationResults.add(proposedUnderlyingAgreementVariationDetailsValidatorService.validate(taskPayload));

        // Validate previous facility ids and application reasons
        validationResults.add(proposedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload));

        return validationResults;
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

        validationResults.forEach(result -> updateViolationMsgWithSectionInfo(result, payloadType));

        return validationResults;
    }

    private BusinessValidationResult updateViolationMsgWithSectionInfo(
            final BusinessValidationResult validationResult,
            final UnderlyingAgreementPayloadType payloadType) {

        validationResult.getViolations().forEach(v -> v.setSectionName(payloadType + v.getSectionName()));

        return validationResult;
    }
}
