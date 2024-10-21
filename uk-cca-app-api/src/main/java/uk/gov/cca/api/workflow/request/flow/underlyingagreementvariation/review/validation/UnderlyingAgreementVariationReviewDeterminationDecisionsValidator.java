package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.ReviewDeterminationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewDeterminationDecisionsValidator {


    public BusinessValidationResult validateOverallDecision(final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload) {
        List<ReviewDeterminationViolation> violations = new ArrayList<>();

        // Validate review groups all exists
        Set<UnderlyingAgreementVariationReviewGroup> groupDiffs = this.containsDecisionForAllUnderlyingAgreementGroups(taskPayload);
        if (!groupDiffs.isEmpty()) {
            violations.add(new ReviewDeterminationViolation(UnderlyingAgreementReviewDecision.class.getName(),
                    ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_REVIEW_DECISION_DATA,
                    groupDiffs));
        }

        // Validate facilities groups has at least one entry of any existent facility
        if (!this.containsDecisionForFacilitiesUnderlyingAgreementGroups(taskPayload)) {
            violations.add(new ReviewDeterminationViolation(UnderlyingAgreementVariationFacilityReviewDecision.class.getName(),
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

    private boolean emptyReviewDecisionExists(UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload) {
        boolean hasInvalidReviewGroupDecisions = taskPayload.getReviewGroupDecisions().values().stream()
                .anyMatch(decision -> decision.getType() == null);
        boolean hasInvalidFacilitiesDecisions = taskPayload.getFacilitiesReviewGroupDecisions().values().stream()
                .anyMatch(decision -> decision.getType() == null);

        return hasInvalidReviewGroupDecisions || hasInvalidFacilitiesDecisions;
    }

    private boolean isValidAccepted(UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload) {

        if (emptyReviewDecisionExists(taskPayload)) {
            return false;
        }

        boolean hasAtLeastOneActiveFacilityAccepted = taskPayload.getFacilitiesReviewGroupDecisions().values().stream()
                .filter(decision -> !decision.getFacilityStatus().equals(FacilityStatus.EXCLUDED))
                .anyMatch(decision -> CcaReviewDecisionType.ACCEPTED.equals(decision.getType()));

        boolean hasAtLeastOneExcludedFacilityRejected = taskPayload.getFacilitiesReviewGroupDecisions().values().stream()
                .filter(decision -> decision.getFacilityStatus().equals(FacilityStatus.EXCLUDED))
                .anyMatch(decision -> CcaReviewDecisionType.REJECTED.equals(decision.getType()));

        // if no active facilities remain in the scheme then the overall decision could not be ACCEPTED
        boolean hasAtLeastOneActiveFacility = hasAtLeastOneActiveFacilityAccepted || hasAtLeastOneExcludedFacilityRejected;

        if (!hasAtLeastOneActiveFacility) {
            return false;
        }

        return taskPayload.getReviewGroupDecisions().values().stream()
                .allMatch(decision -> CcaReviewDecisionType.ACCEPTED.equals(decision.getType()));
    }

    private boolean isValidRejected(UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload) {

        if (emptyReviewDecisionExists(taskPayload)) {
            return false;
        }

        boolean hasAtLeastOneRejectedFacilityDecision = taskPayload.getFacilitiesReviewGroupDecisions().values().stream()
                .anyMatch(decision -> CcaReviewDecisionType.REJECTED.equals(decision.getType()));

        boolean hasAtLeastOneRejectedReviewDecision = taskPayload.getReviewGroupDecisions().values().stream()
                .anyMatch(decision -> CcaReviewDecisionType.REJECTED.equals(decision.getType()));

        return hasAtLeastOneRejectedFacilityDecision || hasAtLeastOneRejectedReviewDecision;
    }


    private Set<UnderlyingAgreementVariationReviewGroup> containsDecisionForAllUnderlyingAgreementGroups(final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload) {
        Map<UnderlyingAgreementVariationReviewGroup, UnderlyingAgreementReviewDecision> underlyingAgreementReviewGroupDecisions = taskPayload
                .getReviewGroupDecisions();

        Set<UnderlyingAgreementVariationReviewGroup> underlyingAgreementReviewGroups = Set.of(UnderlyingAgreementVariationReviewGroup.values());

        return SetUtils.disjunction(underlyingAgreementReviewGroupDecisions.keySet(), underlyingAgreementReviewGroups);
    }

    private boolean containsDecisionForFacilitiesUnderlyingAgreementGroups(final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload) {
        Set<String> facilitiesReviewGroupDecisions = taskPayload.getFacilitiesReviewGroupDecisions().keySet();
        Set<String> facilityKeys = taskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities()
                .stream().map(facility -> facility.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());

        return !facilitiesReviewGroupDecisions.isEmpty() && facilityKeys.containsAll(facilitiesReviewGroupDecisions);
    }
}
