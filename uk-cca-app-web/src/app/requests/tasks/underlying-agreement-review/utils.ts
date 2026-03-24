import {
  OVERALL_DECISION_SUBTASK,
  staticReviewGroupDecisions,
  TaskItemStatus,
  transformAccountReferenceData,
  UNAReviewRequestTaskPayload,
} from '@requests/common';
import { produce } from 'immer';

import { Determination, UnderlyingAgreementFacilityReviewDecision, UnderlyingAgreementPayload } from 'cca-api';

type DecisionType = UnderlyingAgreementFacilityReviewDecision['type'];

const allStatisSectionsAccepted = (payload: UNAReviewRequestTaskPayload) =>
  staticReviewGroupDecisions.every((d) => payload.reviewGroupDecisions[d]?.type === 'ACCEPTED');

const anyStaticSectionRejected = (payload: UNAReviewRequestTaskPayload) =>
  staticReviewGroupDecisions.some((d) => payload.reviewGroupDecisions[d].type === 'REJECTED');

const acceptedFacilityDecisionExists = (payload: UNAReviewRequestTaskPayload) =>
  Object.keys(payload.facilitiesReviewGroupDecisions).some(
    (k) => payload.facilitiesReviewGroupDecisions[k]?.type === 'ACCEPTED',
  );

const rejectedFacilityDecisionExists = (payload: UNAReviewRequestTaskPayload) =>
  Object.keys(payload.facilitiesReviewGroupDecisions).some(
    (k) => payload.facilitiesReviewGroupDecisions[k]?.type === 'REJECTED',
  );

export const reviewSectionsCompleted = (payload: UNAReviewRequestTaskPayload) => {
  const hasUndecidedSection = Object.keys(payload.reviewSectionsCompleted)
    .filter((k) => k !== OVERALL_DECISION_SUBTASK)
    .some((k) => payload.reviewSectionsCompleted[k] === TaskItemStatus.UNDECIDED);

  if (hasUndecidedSection) return false;

  const groupDecisionsCompleted = staticReviewGroupDecisions.every((s) => payload.reviewGroupDecisions[s]);
  if (!groupDecisionsCompleted) return false;

  return payload.underlyingAgreement.facilities.every((f) => payload.facilitiesReviewGroupDecisions[f.facilityId]);
};

export const canAcceptPayload = (payload: UNAReviewRequestTaskPayload) =>
  allStatisSectionsAccepted(payload) && acceptedFacilityDecisionExists(payload);

export const canRejectPayload = (payload: UNAReviewRequestTaskPayload) =>
  anyStaticSectionRejected(payload) || rejectedFacilityDecisionExists(payload);

export const payloadAvailableDecisions = (payload: UNAReviewRequestTaskPayload): DecisionType[] => {
  const actions: DecisionType[] = [];
  if (canAcceptPayload(payload)) actions.push('ACCEPTED');
  if (canRejectPayload(payload)) actions.push('REJECTED');
  return actions;
};

export const createProposedUnderlyingAgreementPayload = (
  payload: UNAReviewRequestTaskPayload,
): UnderlyingAgreementPayload => {
  const { reviewGroupDecisions, facilitiesReviewGroupDecisions, underlyingAgreement, accountReferenceData } = payload;

  const proposed: UnderlyingAgreementPayload = {
    underlyingAgreementTargetUnitDetails: underlyingAgreement.underlyingAgreementTargetUnitDetails,
    facilities: [],
    targetPeriod5Details: underlyingAgreement.targetPeriod5Details,
    targetPeriod6Details: underlyingAgreement.targetPeriod6Details,
    authorisationAndAdditionalEvidence: underlyingAgreement.authorisationAndAdditionalEvidence,
  };

  if (reviewGroupDecisions['TARGET_UNIT_DETAILS']?.type === TaskItemStatus.REJECTED) {
    proposed.underlyingAgreementTargetUnitDetails = transformAccountReferenceData(accountReferenceData);
  }

  proposed.facilities = Object.entries(facilitiesReviewGroupDecisions)
    .filter(([_, groupDecision]) => groupDecision?.type === 'ACCEPTED')
    .map(([key]) => underlyingAgreement.facilities.find((f) => f.facilityId === key));

  return proposed;
};

export function resetDetermination(determination: Determination): Determination {
  return produce(determination, (draft) => {
    if (draft?.type) draft.type = null;
  });
}

export function resetDeterminationStatus(reviewSectionsCompleted: Record<string, string>): Record<string, string> {
  return produce(reviewSectionsCompleted, (draft) => {
    draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
  });
}

/**
 * When the user edits a payload, we need to reset the determination and the sections completed
 */
export function applySaveActionSideEffects(
  determination: Determination,
  reviewSectionsCompleted: Record<string, string>,
  sectionsCompleted: Record<string, string>,
  subtask: string,
): {
  determination: Determination;
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
