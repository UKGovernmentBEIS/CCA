import {
  DECISION_TO_SUBTASK_MAP,
  OVERALL_DECISION_SUBTASK,
  staticVariationGroupDecisions,
  staticVariationSections,
  staticVariationSectionsWithoutBaselineAndTargets,
  SUBTASK_TO_DECISION_MAP,
  TaskItemStatus,
  transformAccountReferenceData,
  UNAVariationReviewRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { produce } from 'immer';

import {
  Facility,
  UnderlyingAgreementContainer,
  UnderlyingAgreementReviewDecision,
  UnderlyingAgreementVariationFacilityReviewDecision,
  UnderlyingAgreementVariationPayload,
  VariationDetermination,
} from 'cca-api';

type DecisionType = UnderlyingAgreementVariationFacilityReviewDecision['type'];
type FacilityWithDecision = Facility & { decisionType: 'ACCEPTED' | 'REJECTED' };

const assertCondition: (condition: unknown, message: string) => asserts condition = (condition, message) => {
  if (!condition) {
    throw new Error(message);
  }
};

const assertFacilityDecisionReferences = (
  facilitiesReviewGroupDecisions: Record<string, UnderlyingAgreementVariationFacilityReviewDecision>,
  facilities: Facility[],
) => {
  const facilityIds = new Set(facilities.map((facility) => facility.facilityId));
  const invalidFacilityId = Object.keys(facilitiesReviewGroupDecisions).find(
    (facilityId) => !facilityIds.has(facilityId),
  );

  assertCondition(
    !invalidFacilityId,
    `Malformed payload: facilitiesReviewGroupDecisions contains unknown facilityId ${invalidFacilityId}`,
  );
};

const assertRejectedFacilityExistsInOriginal = (
  facility: FacilityWithDecision,
  originalUnderlyingAgreementContainer: UnderlyingAgreementContainer,
) => {
  const originalFacility = originalUnderlyingAgreementContainer.underlyingAgreement.facilities.find(
    (original) => original.facilityId === facility.facilityId,
  );

  assertCondition(
    originalFacility,
    `Malformed payload: originalUnderlyingAgreementContainer is missing facilityId ${facility.facilityId}`,
  );

  return originalFacility;
};

const requiredDecisions = (payload: UNAVariationReviewRequestTaskPayload) =>
  payload.underlyingAgreement.targetPeriod5Details
    ? staticVariationGroupDecisions
    : staticVariationSectionsWithoutBaselineAndTargets.map((subtask) => SUBTASK_TO_DECISION_MAP[subtask]);

const acceptedSectionExists = (payload: UNAVariationReviewRequestTaskPayload) =>
  requiredDecisions(payload).some((d) => payload.reviewGroupDecisions[d]?.type === 'ACCEPTED');

const rejectedSectionExists = (payload: UNAVariationReviewRequestTaskPayload) =>
  requiredDecisions(payload).some((d) => payload.reviewGroupDecisions[d]?.type === 'REJECTED');

const acceptedFacilityDecisionExists = (payload: UNAVariationReviewRequestTaskPayload) =>
  Object.keys(payload.facilitiesReviewGroupDecisions).some(
    (k) => payload.facilitiesReviewGroupDecisions[k]?.type === 'ACCEPTED',
  );

const rejectedFacilityDecisionExists = (payload: UNAVariationReviewRequestTaskPayload) =>
  Object.keys(payload.facilitiesReviewGroupDecisions).some(
    (k) => payload.facilitiesReviewGroupDecisions[k]?.type === 'REJECTED',
  );

const notRejectedAndNew = (f: FacilityWithDecision) => !(f.decisionType === 'REJECTED' && f.status === 'NEW');

const rejectedToOriginal =
  (originalUnderlyingAgreementContainer: UnderlyingAgreementContainer) => (facility: FacilityWithDecision) =>
    facility.decisionType === 'REJECTED'
      ? assertRejectedFacilityExistsInOriginal(facility, originalUnderlyingAgreementContainer)
      : toFacility(facility);

const toFacility = (f: FacilityWithDecision) =>
  produce(f, (facility) => {
    delete facility.decisionType;
  });

export const reviewSectionsCompleted = (payload: UNAVariationReviewRequestTaskPayload) => {
  if (
    !payload.reviewSectionsCompleted[VARIATION_DETAILS_SUBTASK] ||
    Object.entries(payload.reviewSectionsCompleted).some(
      ([key, status]) => key !== OVERALL_DECISION_SUBTASK && status === TaskItemStatus.UNDECIDED,
    )
  )
    return false;

  // We need to filter out any decisions that are not required for the current agreement (unchanged status)
  const finalRequiredDecisions = requiredDecisions(payload).filter(
    (decision) => payload.reviewGroupDecisions[decision],
  );

  // We don't need to filter any facilities, because the overall status is already checked for the manage facility
  const requiredFacilityDecisions = payload.underlyingAgreement.facilities.map((f) => f.facilityId);

  return (
    finalRequiredDecisions.every((decision) => payload.reviewGroupDecisions[decision]) &&
    payload.underlyingAgreement.facilities.every((f) => requiredFacilityDecisions.includes(f.facilityId))
  );
};

export const validFacilityExists = (payload: UNAVariationReviewRequestTaskPayload) => {
  const activeExcluded = Object.keys(payload.facilitiesReviewGroupDecisions).some(
    (k) =>
      payload.facilitiesReviewGroupDecisions[k]?.facilityStatus === 'EXCLUDED' &&
      payload.facilitiesReviewGroupDecisions[k]?.type === 'REJECTED',
  );

  const activeLive = Object.keys(payload.facilitiesReviewGroupDecisions).some(
    (k) => payload.facilitiesReviewGroupDecisions[k]?.facilityStatus === 'LIVE',
  );

  const activeNew = Object.keys(payload.facilitiesReviewGroupDecisions).some(
    (k) =>
      payload.facilitiesReviewGroupDecisions[k]?.facilityStatus === 'NEW' &&
      payload.facilitiesReviewGroupDecisions[k]?.type === 'ACCEPTED',
  );

  const unchanged = payload.underlyingAgreement.facilities.some(
    (k) => payload.reviewSectionsCompleted[k.facilityId] === TaskItemStatus.UNCHANGED,
  );

  return activeExcluded || activeLive || activeNew || unchanged;
};

export const canAcceptVariationPayload = (payload: UNAVariationReviewRequestTaskPayload) => {
  return (acceptedSectionExists(payload) || acceptedFacilityDecisionExists(payload)) && validFacilityExists(payload);
};

export const canRejectVariationPayload = (payload: UNAVariationReviewRequestTaskPayload) =>
  rejectedSectionExists(payload) || rejectedFacilityDecisionExists(payload) || !validFacilityExists(payload);

export const payloadAvailableDecisions = (payload: UNAVariationReviewRequestTaskPayload): DecisionType[] => {
  const actions: DecisionType[] = [];
  if (canAcceptVariationPayload(payload)) actions.push('ACCEPTED');
  if (canRejectVariationPayload(payload)) actions.push('REJECTED');
  return actions;
};

/**
 *
 * The proposed underlying agreement is a structure that includes the result of applying the
 * decisions over the UNAVariationReviewRequestTaskPayload.
 *
 * For each REJECTED subtask, we keep the data from the originalUnderlyingAgreementContainer.
 * For each ACCEPTED subtask, we keep the data from the current underlyingAgreement.
 */
export const createProposedUnderlyingAgreementVariationPayload = (
  payload: UNAVariationReviewRequestTaskPayload,
): UnderlyingAgreementVariationPayload => {
  const {
    originalUnderlyingAgreementContainer,
    reviewGroupDecisions,
    facilitiesReviewGroupDecisions,
    underlyingAgreement,
    accountReferenceData,
    reviewSectionsCompleted,
  } = payload;

  assertCondition(
    underlyingAgreement && Array.isArray(underlyingAgreement.facilities),
    'Malformed payload: underlyingAgreement.facilities is required to create proposed underlying agreement payload',
  );

  assertCondition(
    reviewSectionsCompleted,
    'Malformed payload: reviewSectionsCompleted is required to create proposed underlying agreement payload',
  );

  assertCondition(
    reviewGroupDecisions,
    'Malformed payload: reviewGroupDecisions is required to create proposed underlying agreement payload',
  );

  assertCondition(
    facilitiesReviewGroupDecisions,
    'Malformed payload: facilitiesReviewGroupDecisions is required to create proposed underlying agreement payload',
  );

  assertCondition(
    originalUnderlyingAgreementContainer &&
      originalUnderlyingAgreementContainer.underlyingAgreement &&
      Array.isArray(originalUnderlyingAgreementContainer.underlyingAgreement.facilities),
    'Malformed payload: originalUnderlyingAgreementContainer.underlyingAgreement.facilities is required to create proposed underlying agreement payload',
  );

  assertFacilityDecisionReferences(facilitiesReviewGroupDecisions, underlyingAgreement.facilities);

  const unchangedFacilities: Facility[] = underlyingAgreement.facilities.filter(
    (f) => reviewSectionsCompleted[f.facilityId] === TaskItemStatus.UNCHANGED,
  );

  const proposed: UnderlyingAgreementVariationPayload = {
    underlyingAgreementVariationDetails: underlyingAgreement.underlyingAgreementVariationDetails,
    underlyingAgreementTargetUnitDetails: underlyingAgreement.underlyingAgreementTargetUnitDetails,
    facilities: [],
    targetPeriod5Details: underlyingAgreement.targetPeriod5Details,
    targetPeriod6Details: underlyingAgreement.targetPeriod6Details,
    authorisationAndAdditionalEvidence: underlyingAgreement.authorisationAndAdditionalEvidence,
  };

  if (reviewGroupDecisions['TARGET_UNIT_DETAILS']?.type === TaskItemStatus.REJECTED) {
    assertCondition(
      accountReferenceData,
      'Malformed payload: accountReferenceData is required when TARGET_UNIT_DETAILS is REJECTED',
    );
    proposed.underlyingAgreementTargetUnitDetails = transformAccountReferenceData(accountReferenceData);
  }

  Object.entries(reviewGroupDecisions)
    .filter(([reviewGroup]) => reviewGroup !== 'TARGET_UNIT_DETAILS' && reviewGroup !== 'VARIATION_DETAILS')
    .forEach(([reviewGroup, decision]) => {
      if (decision.type === TaskItemStatus.REJECTED)
        proposed[DECISION_TO_SUBTASK_MAP[reviewGroup]] =
          originalUnderlyingAgreementContainer.underlyingAgreement[DECISION_TO_SUBTASK_MAP[reviewGroup]];
    });

  proposed.facilities = Object.keys(facilitiesReviewGroupDecisions)
    .map((k) => ({
      decisionType: facilitiesReviewGroupDecisions[k].type,
      ...underlyingAgreement.facilities.find((f) => f.facilityId === k),
    }))
    .filter(notRejectedAndNew)
    .map(rejectedToOriginal(originalUnderlyingAgreementContainer));

  proposed.facilities = [...proposed.facilities, ...unchangedFacilities];

  return proposed;
};

export function deleteDecision(
  reviewGroupDecisions: Record<string, UnderlyingAgreementReviewDecision>,
  subtask: string,
): Record<string, UnderlyingAgreementReviewDecision> {
  return produce(reviewGroupDecisions, (draft) => {
    if (draft[subtask]?.type) {
      delete draft[subtask];
    }
  });
}

export function deleteFacilityDecision(
  reviewGroupDecisions: Record<string, UnderlyingAgreementVariationFacilityReviewDecision>,
  facilityId: string,
): Record<string, UnderlyingAgreementVariationFacilityReviewDecision> {
  return produce(reviewGroupDecisions, (draft) => {
    if (draft[facilityId]?.type) {
      delete draft[facilityId];
    }
  });
}

export function resetDetermination(determination: VariationDetermination | undefined): VariationDetermination {
  return produce(determination, (draft) => {
    if (draft?.type) {
      draft.type = null;
    }
  });
}

/**
 * When the user edits a payload, we need to reset the determination and the sections completed
 */
export function applySaveActionSideEffects(
  determination: VariationDetermination,
  reviewSectionsCompleted: Record<string, string>,
  sectionsCompleted: Record<string, string>,
  subtask: string,
): {
  determination: VariationDetermination;
  reviewSectionsCompleted: Record<string, string>;
  sectionsCompleted: Record<string, string>;
} {
  return {
    determination: resetDetermination(determination),
    reviewSectionsCompleted: produce(reviewSectionsCompleted, (draft) => {
      draft[subtask] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    }),
    // we used to reset the sections completed, but this is not needed anymore and we want to keep the function's api the same
    sectionsCompleted: produce(sectionsCompleted, (draft) => {
      draft[subtask] = TaskItemStatus.IN_PROGRESS;
    }),
  };
}

export function calcManageFacilitiesStatus(reviewSectionsCompleted: Record<string, string>): TaskItemStatus {
  const facilitySections = Object.keys(reviewSectionsCompleted).filter(
    (s) => ![...staticVariationSections, OVERALL_DECISION_SUBTASK].includes(s),
  );

  if (facilitySections.length === 0) return TaskItemStatus.UNDECIDED;

  const allFacilitiesAccepted = facilitySections.every((s) => reviewSectionsCompleted?.[s] === TaskItemStatus.ACCEPTED);
  if (allFacilitiesAccepted) return TaskItemStatus.ACCEPTED;

  const allFacilitiesRejected = facilitySections.every((s) => reviewSectionsCompleted?.[s] === TaskItemStatus.REJECTED);
  if (allFacilitiesRejected) return TaskItemStatus.REJECTED;

  const undecidedFacilityExists = facilitySections.some(
    (s) => reviewSectionsCompleted?.[s] === TaskItemStatus.UNDECIDED,
  );

  if (undecidedFacilityExists) return TaskItemStatus.UNDECIDED;

  const allFacilitiesUnchanged = facilitySections.every(
    (s) => reviewSectionsCompleted?.[s] === TaskItemStatus.UNCHANGED,
  );

  if (allFacilitiesUnchanged) return TaskItemStatus.UNCHANGED;

  return facilitySections.some((s) => reviewSectionsCompleted?.[s] === TaskItemStatus.ACCEPTED)
    ? TaskItemStatus.ACCEPTED
    : TaskItemStatus.REJECTED;
}
