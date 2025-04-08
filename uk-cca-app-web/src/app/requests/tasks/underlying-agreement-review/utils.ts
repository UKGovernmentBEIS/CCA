import {
  OVERALL_DECISION_SUBTASK,
  staticGroupDecisions,
  TaskItemStatus,
  transform,
  UNAReviewRequestTaskPayload,
} from '@requests/common';

import { UnderlyingAgreementFacilityReviewDecision, UnderlyingAgreementPayload } from 'cca-api';

type DecisionType = UnderlyingAgreementFacilityReviewDecision['type'];

const allStatisSectionsAccepted = (payload: UNAReviewRequestTaskPayload) =>
  staticGroupDecisions.every((d) => payload.reviewGroupDecisions[d]?.type === 'ACCEPTED');

const anyStaticSectionRejected = (payload: UNAReviewRequestTaskPayload) =>
  staticGroupDecisions.some((d) => payload.reviewGroupDecisions[d].type === 'REJECTED');

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

  const groupDecisionsCompleted = staticGroupDecisions.every((s) => payload.reviewGroupDecisions[s]);
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

  if (reviewGroupDecisions['TARGET_UNIT_DETAILS'].type === 'REJECTED')
    proposed.underlyingAgreementTargetUnitDetails = transform(accountReferenceData);

  proposed.facilities = Object.entries(facilitiesReviewGroupDecisions)
    .filter((frg) => frg[1].type === 'ACCEPTED')
    .map((frg) => underlyingAgreement.facilities.find((f) => f.facilityId === frg[0]));

  return proposed;
};
