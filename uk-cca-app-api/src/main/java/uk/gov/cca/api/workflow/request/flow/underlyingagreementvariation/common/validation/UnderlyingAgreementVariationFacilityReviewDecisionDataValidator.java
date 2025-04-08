package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.ReviewDeterminationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationFacilityReviewDecisionDataValidator {

    private final DataValidator<UnderlyingAgreementVariationFacilityReviewDecision> dataValidator;

    public BusinessValidationResult validateUnderlyingAgreementVariationFacilityReviewDecisions(final UnderlyingAgreementVariationRequestTaskPayload taskPayload) {
        List<ReviewDeterminationViolation> violations = new ArrayList<>();

        // Validate data decision facilities review groups
        taskPayload.getFacilitiesReviewGroupDecisions().values()
                .forEach(decision -> dataValidator.validate(decision).stream()
                        .map(businessViolation ->
                                new ReviewDeterminationViolation(UnderlyingAgreementVariationFacilityReviewDecision.class.getName(),
                                        ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_FACILITY_REVIEW_DECISION_DATA,
                                        businessViolation.getData())).findFirst()
                        .ifPresent(violations::add));

        // Validate facility ids
        validateFacilitiesInReviewGroups(taskPayload).ifPresent(violations::add);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
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

        if(!diff.isEmpty()) {
            return  Optional.of(new ReviewDeterminationViolation(UnderlyingAgreementVariationFacilityReviewDecision.class.getName(),
                    ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_FACILITY_REVIEW_DECISION_DATA,
                    diff));
        }

        return Optional.empty();
    }
}
