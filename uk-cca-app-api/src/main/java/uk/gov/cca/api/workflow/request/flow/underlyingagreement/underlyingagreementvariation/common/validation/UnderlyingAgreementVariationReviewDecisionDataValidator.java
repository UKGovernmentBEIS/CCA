package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.ReviewDeterminationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewDecisionDataValidator {

    private final DataValidator<UnderlyingAgreementReviewDecision> reviewDecisionDataValidator;
    private final DataValidator<UnderlyingAgreementVariationFacilityReviewDecision> facilityReviewDecisionDataValidator;
    private final DataValidator<VariationDetermination> determinationDataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;


    public BusinessValidationResult validateReviewDecisionData(final RequestTask requestTask) {

        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();
        List<ReviewDeterminationViolation> violations = new ArrayList<>();

        // Validate data decision review groups
        validateUnderlyingAgreementReviewGroupDecisions(taskPayload, violations);

        //validate data decision facilities review groups
        validateUnderlyingAgreementFacilityReviewDecisions(taskPayload, violations);

        // Validate determination data
        validateDetermination(taskPayload, violations);

        // Validate review decision files
        validateReviewDecisionFiles(taskPayload, violations);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    public BusinessValidationResult validateFacilityAndGroupReviewDecisions(final UnderlyingAgreementVariationRequestTaskPayload taskPayload) {
        List<ReviewDeterminationViolation> violations = new ArrayList<>();

        // Validate data decision review groups
        validateUnderlyingAgreementReviewGroupDecisions(taskPayload, violations);

        //validate data decision facilities review groups
        validateUnderlyingAgreementFacilityReviewDecisions(taskPayload, violations);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    private void validateUnderlyingAgreementReviewGroupDecisions(final UnderlyingAgreementVariationRequestTaskPayload taskPayload, List<ReviewDeterminationViolation> violations) {
        taskPayload.getReviewGroupDecisions().values()
                .forEach(decision -> reviewDecisionDataValidator.validate(decision).stream()
                        .map(businessViolation ->
                                new ReviewDeterminationViolation(UnderlyingAgreementReviewDecision.class.getName(),
                                        ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_REVIEW_DECISION_DATA,
                                        businessViolation.getData()))
                        .findFirst().ifPresent(violations::add));
    }

    private void validateUnderlyingAgreementFacilityReviewDecisions(final UnderlyingAgreementVariationRequestTaskPayload taskPayload, List<ReviewDeterminationViolation> violations) {
        // Validate data decision facilities review groups
        taskPayload.getFacilitiesReviewGroupDecisions().values()
                .forEach(decision -> facilityReviewDecisionDataValidator.validate(decision).stream()
                        .map(businessViolation ->
                                new ReviewDeterminationViolation(UnderlyingAgreementVariationFacilityReviewDecision.class.getName(),
                                        ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_FACILITY_REVIEW_DECISION_DATA,
                                        businessViolation.getData())).findFirst()
                        .ifPresent(violations::add));

        // Validate facility ids
        validateFacilitiesInReviewGroups(taskPayload).ifPresent(violations::add);
    }


    /*
     * Validate if facility ids in group decisions is of existent facilities.
     * As such we need the difference between sets and not the disjunction
     */
    private Optional<ReviewDeterminationViolation> validateFacilitiesInReviewGroups(final UnderlyingAgreementVariationRequestTaskPayload taskPayload) {
        Set<String> groups = taskPayload.getFacilitiesReviewGroupDecisions().keySet();
        Set<String> facilities = taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities()
                .stream().map(facility -> facility.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());
        Set<String> diff = SetUtils.difference(groups, facilities);

        if (!diff.isEmpty()) {
            return Optional.of(new ReviewDeterminationViolation(UnderlyingAgreementVariationFacilityReviewDecision.class.getName(),
                    ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_FACILITY_REVIEW_DECISION_DATA,
                    diff));
        }

        return Optional.empty();
    }

    private void validateDetermination(final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload, List<ReviewDeterminationViolation> violations) {
        // Validate not empty Determination
        validateNotEmptyDetermination(taskPayload, violations);

        // Validate determination data
        determinationDataValidator.validate(taskPayload.getDetermination())
                .map(businessViolation ->
                        new ReviewDeterminationViolation(VariationDetermination.class.getName(),
                                ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_DETERMINATION_DATA,
                                businessViolation.getData()))
                .ifPresent(violations::add);
    }

    private void validateNotEmptyDetermination(final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload, List<ReviewDeterminationViolation> violations) {
        // Validate not empty Determination
        if (taskPayload.getDetermination() == null) {
            violations.add(new ReviewDeterminationViolation(VariationDetermination.class.getName(),
                    ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_DETERMINATION_DATA));
        }
    }

    private void validateReviewDecisionFiles(final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload, List<ReviewDeterminationViolation> violations) {
        if (!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments().keySet())) {
            violations.add(new ReviewDeterminationViolation(ReviewDeterminationViolation.ReviewDeterminationViolationMessage.ATTACHMENT_NOT_FOUNT));
        }
    }
}
