package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.ReviewDeterminationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementReviewDecisionDataValidator {

    private final DataValidator<UnderlyingAgreementReviewDecision> reviewDecisionDataValidator;
    private final DataValidator<UnderlyingAgreementFacilityReviewDecision> facilityReviewDecisionDataValidator;
    private final DataValidator<Determination> determinationDataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public BusinessValidationResult validateReviewDecisionData(final RequestTask requestTask) {

        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();
        List<ReviewDeterminationViolation> violations = new ArrayList<>();

        //validate data decision review groups
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

    private void validateUnderlyingAgreementReviewGroupDecisions(final UnderlyingAgreementReviewRequestTaskPayload taskPayload, List<ReviewDeterminationViolation> violations) {
        taskPayload.getReviewGroupDecisions().values()
                .forEach(decision -> reviewDecisionDataValidator.validate(decision).stream()
                        .map(businessViolation ->
                                new ReviewDeterminationViolation(UnderlyingAgreementReviewDecision.class.getName(),
                                        ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_REVIEW_DECISION_DATA,
                                        businessViolation.getData()))
                        .findFirst().ifPresent(violations::add));
    }

    private void validateUnderlyingAgreementFacilityReviewDecisions(final UnderlyingAgreementReviewRequestTaskPayload taskPayload, List<ReviewDeterminationViolation> violations) {
        taskPayload.getFacilitiesReviewGroupDecisions().values()
                .forEach(decision -> facilityReviewDecisionDataValidator.validate(decision).stream()
                        .map(businessViolation ->
                                new ReviewDeterminationViolation(UnderlyingAgreementFacilityReviewDecision.class.getName(),
                                        ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_FACILITY_REVIEW_DECISION_DATA,
                                        businessViolation.getData())).findFirst()
                        .ifPresent(violations::add));
    }

    private void validateDetermination(final UnderlyingAgreementReviewRequestTaskPayload taskPayload, List<ReviewDeterminationViolation> violations) {
        validateNotEmptyDetermination(taskPayload, violations);

        determinationDataValidator.validate(taskPayload.getDetermination())
                .map(businessViolation ->
                        new ReviewDeterminationViolation(Determination.class.getName(),
                                ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_DETERMINATION_DATA,
                                businessViolation.getData()))
                .ifPresent(violations::add);
    }

    private void validateNotEmptyDetermination(final UnderlyingAgreementReviewRequestTaskPayload taskPayload, List<ReviewDeterminationViolation> violations) {
        // Validate not empty Determination
        if (taskPayload.getDetermination() == null) {
            violations.add(new ReviewDeterminationViolation(Determination.class.getName(),
                    ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_DETERMINATION_DATA));
        }
    }

    private void validateReviewDecisionFiles(final UnderlyingAgreementReviewRequestTaskPayload taskPayload, List<ReviewDeterminationViolation> violations) {
        if (!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments().keySet())) {
            violations.add(new ReviewDeterminationViolation(ReviewDeterminationViolation.ReviewDeterminationViolationMessage.ATTACHMENT_NOT_FOUNT));
        }
    }
}