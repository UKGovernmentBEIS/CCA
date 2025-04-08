package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.ReviewDeterminationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewDecisionDataValidator {

    private final DataValidator<UnderlyingAgreementReviewDecision> dataValidator;

    public BusinessValidationResult validateUnderlyingAgreementVariationReviewDecisions(final UnderlyingAgreementVariationRequestTaskPayload taskPayload) {
        List<ReviewDeterminationViolation> violations = new ArrayList<>();

        // Validate data decision review groups
        taskPayload.getReviewGroupDecisions().values()
                .forEach(decision -> dataValidator.validate(decision).stream()
                        .map(businessViolation ->
                                new ReviewDeterminationViolation(ReviewDeterminationViolation.class.getName(),
                                        ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_REVIEW_DECISION_DATA,
                                        businessViolation.getData())).findFirst()
                        .ifPresent(violations::add));

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
