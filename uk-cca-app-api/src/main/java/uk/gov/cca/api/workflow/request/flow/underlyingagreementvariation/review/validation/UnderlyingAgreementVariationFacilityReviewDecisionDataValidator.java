package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.ReviewDeterminationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationFacilityReviewDecisionDataValidator {

    private final DataValidator<UnderlyingAgreementVariationFacilityReviewDecision> dataValidator;

    public BusinessValidationResult validateUnderlyingAgreementVariationFacilityReviewDecisions(final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload) {
        List<ReviewDeterminationViolation> violations = new ArrayList<>();

        //validate data decision facilities review groups
        taskPayload.getFacilitiesReviewGroupDecisions().values()
                .forEach(decision -> dataValidator.validate(decision).stream()
                        .map(businessViolation ->
                                new ReviewDeterminationViolation(UnderlyingAgreementVariationFacilityReviewDecision.class.getName(),
                                        ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_FACILITY_REVIEW_DECISION_DATA,
                                        businessViolation.getData())).findFirst()
                        .ifPresent(violations::add));

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
