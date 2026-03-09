package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementFacilityAgainstCca2EndDateValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.CCA2BaselineAndTargetsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.transform.UnderlyingAgreementContainerMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.validation.EditedUnderlyingAgreementTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.transform.UnderlyingAgreementReviewMapper;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementPayloadType.EDITED;
import static uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementPayloadType.PROPOSED;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementReviewValidatorService {

    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final CCA2BaselineAndTargetsValidatorService cca2BaselineAndTargetsValidatorService;
    private final UnderlyingAgreementFacilityAgainstCca2EndDateValidatorService underlyingAgreementCca2EndDateValidatorService;
    private final EditedUnderlyingAgreementTargetUnitDetailsValidatorService editedUnderlyingAgreementTargetUnitDetailsValidatorService;
    private final ProposedUnderlyingAgreementTargetUnitDetailsValidatorService proposedUnderlyingAgreementTargetUnitDetailsValidatorService;

    private static final UnderlyingAgreementContainerMapper UNDERLYING_AGREEMENT_CONTAINER_MAPPER = Mappers.getMapper(UnderlyingAgreementContainerMapper.class);
    private static final UnderlyingAgreementReviewMapper UNDERLYING_AGREEMENT_REVIEW_MAPPER = Mappers.getMapper(UnderlyingAgreementReviewMapper.class);

    public List<BusinessValidationResult> validateEditedUnderlyingAgreement(final RequestTask requestTask) {
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        final UnderlyingAgreementContainer editedUnaContainer = UNDERLYING_AGREEMENT_CONTAINER_MAPPER.toUnderlyingAgreementContainer(taskPayload);
        
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(taskPayload.getWorkflowSchemeVersion())
                .requestCreationDate(requestTask.getRequest().getCreationDate())
                .build();
        
        List<BusinessValidationResult> validationResults = this.updateViolationMsgWithSectionInfo(
                underlyingAgreementValidatorService.getValidationResults(editedUnaContainer, underlyingAgreementValidationContext), EDITED);

        // Validate target period details
        validationResults.add(this.updateViolationMsgWithSectionInfo(
                cca2BaselineAndTargetsValidatorService.validateEmpty(editedUnaContainer),
                EDITED
        ));

        UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) requestTask.getRequest().getPayload();

        // Validate if Sector user's facilityIds match Regulator
        validationResults.add(validateEditedFacilityIds(taskPayload, requestPayload));

        // Validate target unit
        validationResults.add(editedUnderlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask));

        return validationResults;
    }


    public List<BusinessValidationResult> validateProposedUnderlyingAgreement(final RequestTask requestTask) {
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(requestTask.getRequest().getCreationDate())
                .schemeVersion(taskPayload.getWorkflowSchemeVersion())
                .build();
        
        UnderlyingAgreementContainer proposedUnaContainer = UNDERLYING_AGREEMENT_REVIEW_MAPPER.toUnderlyingAgreementProposedContainer(taskPayload);

        List<BusinessValidationResult> validationResults;
        if (hasAllFacilitiesRejected(taskPayload)) {
        	validationResults = this.updateViolationMsgWithSectionInfo(
                    underlyingAgreementValidatorService.getValidationResultsExceptFacilities(proposedUnaContainer, underlyingAgreementValidationContext), PROPOSED);
        } else {
        	validationResults = this.updateViolationMsgWithSectionInfo(
                    underlyingAgreementValidatorService.getValidationResults(proposedUnaContainer, underlyingAgreementValidationContext), PROPOSED);
        }

        // Validate target period details
        validationResults.add(this.updateViolationMsgWithSectionInfo(
                cca2BaselineAndTargetsValidatorService.validateEmpty(proposedUnaContainer),
                PROPOSED
        ));

        UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) requestTask.getRequest().getPayload();

        // Validate if Sector user's facilityIds match Regulator
        validationResults.add(validateProposedFacilityIds(taskPayload, requestPayload));
        
        // Validate CCA2 end date related rules for facilities
        validationResults.add(underlyingAgreementCca2EndDateValidatorService.validate(proposedUnaContainer.getUnderlyingAgreement().getFacilities()));

        validationResults.add(proposedUnderlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask));

        return validationResults;
    }


	private boolean hasAllFacilitiesRejected(final UnderlyingAgreementReviewRequestTaskPayload taskPayload) {
		return taskPayload.getProposedUnderlyingAgreement().getUnderlyingAgreement().getFacilities().isEmpty()
				&& taskPayload.getFacilitiesReviewGroupDecisions().values()
				.stream()
				.allMatch(decision -> CcaReviewDecisionType.REJECTED.equals(decision.getType()));
	}

    private BusinessValidationResult validateEditedFacilityIds(UnderlyingAgreementReviewRequestTaskPayload taskPayload,
                                                               UnderlyingAgreementRequestPayload requestPayload) {
        Set<String> facilityIds = taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities().stream()
                .map(facility -> facility.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());
        Set<String> sectorUserFacilityIds = requestPayload.getFacilityIds();
        Set<String> diffs = SetUtils.disjunction(facilityIds, sectorUserFacilityIds);
        if (!diffs.isEmpty()) {
            return BusinessValidationResult.builder()
                    .valid(false)
                    .violations(List.of(
                            new UnderlyingAgreementViolation(EDITED + Facility.class.getName(),
                                    UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_SECTION_DATA, diffs)
                    ))
                    .build();
        }

        return BusinessValidationResult.valid();
    }

    private BusinessValidationResult validateProposedFacilityIds(UnderlyingAgreementReviewRequestTaskPayload taskPayload,
                                                                 UnderlyingAgreementRequestPayload requestPayload) {
        Set<String> facilityIds = taskPayload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities().stream()
                .map(facility -> facility.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());
        Set<String> sectorUserFacilityIds = requestPayload.getFacilityIds();
        Set<String> diffs = SetUtils.difference(facilityIds, sectorUserFacilityIds);
        if (!diffs.isEmpty()) {
            return BusinessValidationResult.builder()
                    .valid(false)
                    .violations(List.of(
                            new UnderlyingAgreementViolation(PROPOSED + Facility.class.getName(),
                                    UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_SECTION_DATA, diffs)
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
