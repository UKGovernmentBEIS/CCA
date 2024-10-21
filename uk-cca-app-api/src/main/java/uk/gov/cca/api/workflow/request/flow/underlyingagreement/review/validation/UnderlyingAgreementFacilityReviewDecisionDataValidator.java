package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.ReviewDeterminationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementFacilityReviewDecisionDataValidator {

    private final DataValidator<UnderlyingAgreementFacilityReviewDecision> dataValidator;

    public BusinessValidationResult validateUnderlyingAgreementFacilityReviewDecisions(final UnderlyingAgreementReviewRequestTaskPayload taskPayload) {
        List<ReviewDeterminationViolation> violations = new ArrayList<>();

        //validate data decision facilities review groups
        taskPayload.getFacilitiesReviewGroupDecisions().values()
                .forEach(decision -> dataValidator.validate(decision).stream()
                        .map(businessViolation ->
                                new ReviewDeterminationViolation(UnderlyingAgreementFacilityReviewDecision.class.getName(),
                                        ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_FACILITY_REVIEW_DECISION_DATA,
                                        businessViolation.getData())).findFirst()
                        .ifPresent(violations::add));

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
