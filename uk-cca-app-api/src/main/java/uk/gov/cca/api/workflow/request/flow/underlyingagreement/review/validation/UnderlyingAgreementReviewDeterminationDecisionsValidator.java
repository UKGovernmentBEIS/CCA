package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.ReviewDeterminationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementReviewGroup;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementReviewDeterminationDecisionsValidator {


    public BusinessValidationResult validateOverallDecision(final UnderlyingAgreementReviewRequestTaskPayload taskPayload) {
        List<ReviewDeterminationViolation> violations = new ArrayList<>();

        // Validate review groups all exists
        Set<UnderlyingAgreementReviewGroup> groupDiffs = this.containsDecisionForAllUnderlyingAgreementGroups(taskPayload);
        if (!groupDiffs.isEmpty()) {
            violations.add(new ReviewDeterminationViolation(UnderlyingAgreementReviewDecision.class.getName(),
                    ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_REVIEW_DECISION_DATA,
                    groupDiffs));
        }

        // Validate facilities groups has at least one entry of any existent facility
        if (!this.containsDecisionForFacilitiesUnderlyingAgreementGroups(taskPayload)) {
            violations.add(new ReviewDeterminationViolation(UnderlyingAgreementFacilityReviewDecision.class.getName(),
                    ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_REVIEW_DECISION_DATA,
                    taskPayload.getFacilitiesReviewGroupDecisions().keySet()));
        }

        // Validate review groups statuses accepted and rejected
        if (taskPayload.getDetermination().getType().equals(DeterminationType.ACCEPTED)) {
            if (!this.isValidAccepted(taskPayload)) {
                violations.add(new ReviewDeterminationViolation(Determination.class.getName(),
                        ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_DETERMINATION_DATA));
            }
        } else {
            if (!this.isValidRejected(taskPayload)) {
                violations.add(new ReviewDeterminationViolation(Determination.class.getName(),
                        ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_DETERMINATION_DATA));
            }

        }
        return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
    }

    private boolean emptyReviewDecisionExists(UnderlyingAgreementReviewRequestTaskPayload taskPayload) {
        boolean hasInvalidReviewGroupDecisions = taskPayload.getReviewGroupDecisions().values().stream()
                .anyMatch(decision -> decision.getType() == null);
        boolean hasInvalidFacilitiesDecisions = taskPayload.getFacilitiesReviewGroupDecisions().values().stream()
                .anyMatch(decision -> decision.getType() == null);

        return hasInvalidReviewGroupDecisions || hasInvalidFacilitiesDecisions;
    }

    private boolean isValidAccepted(UnderlyingAgreementReviewRequestTaskPayload taskPayload) {

        if (emptyReviewDecisionExists(taskPayload)) {
            return false;
        }

        boolean isFacilitiesReviewGroupDecisionsValid = taskPayload.getFacilitiesReviewGroupDecisions().values().stream()
                .anyMatch(decision -> CcaReviewDecisionType.ACCEPTED.equals(decision.getType()));

        if (!isFacilitiesReviewGroupDecisionsValid) {
            return false;
        }

        return taskPayload.getReviewGroupDecisions().values().stream()
                .allMatch(decision -> CcaReviewDecisionType.ACCEPTED.equals(decision.getType()));
    }

    private boolean isValidRejected(UnderlyingAgreementReviewRequestTaskPayload taskPayload) {

        if (emptyReviewDecisionExists(taskPayload)) {
            return false;
        }

        boolean hasAtLeastOneRejectedFacilityDecision = taskPayload.getFacilitiesReviewGroupDecisions().values().stream()
                .anyMatch(decision -> CcaReviewDecisionType.REJECTED.equals(decision.getType()));

        boolean hasAtLeastOneRejectedReviewDecision = taskPayload.getReviewGroupDecisions().values().stream()
                .anyMatch(decision -> CcaReviewDecisionType.REJECTED.equals(decision.getType()));

        return hasAtLeastOneRejectedFacilityDecision || hasAtLeastOneRejectedReviewDecision;
    }


    private Set<UnderlyingAgreementReviewGroup> containsDecisionForAllUnderlyingAgreementGroups(final UnderlyingAgreementReviewRequestTaskPayload taskPayload) {
        Map<UnderlyingAgreementReviewGroup, UnderlyingAgreementReviewDecision> underlyingAgreementReviewGroupDecisions = taskPayload
                .getReviewGroupDecisions();

        Set<UnderlyingAgreementReviewGroup> underlyingAgreementReviewGroups = Set.of(UnderlyingAgreementReviewGroup.values());

        return SetUtils.disjunction(underlyingAgreementReviewGroupDecisions.keySet(), underlyingAgreementReviewGroups);
    }

    private boolean containsDecisionForFacilitiesUnderlyingAgreementGroups(final UnderlyingAgreementReviewRequestTaskPayload taskPayload) {
        Set<String> facilitiesReviewGroupDecisions = taskPayload.getFacilitiesReviewGroupDecisions().keySet();
        Set<String> facilityKeys = taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities()
                .stream().map(facility -> facility.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());

        return !facilitiesReviewGroupDecisions.isEmpty() && facilityKeys.containsAll(facilitiesReviewGroupDecisions);
    }
}
