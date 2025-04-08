import {
  OVERALL_DECISION_SUBTASK,
  staticVariationGroupDecisions,
  StaticVariationGroupDecisionsEnum,
  TaskItemStatus,
  transform,
  UNAVariationReviewRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { produce } from 'immer';

import {
  Facility,
  UnderlyingAgreementContainer,
  UnderlyingAgreementVariationFacilityReviewDecision,
  UnderlyingAgreementVariationPayload,
} from 'cca-api';

type DecisionType = UnderlyingAgreementVariationFacilityReviewDecision['type'];
type FacilityWithDecision = Facility & { decisionType: 'ACCEPTED' | 'REJECTED' };

const acceptedSectionExists = (payload: UNAVariationReviewRequestTaskPayload) =>
  staticVariationGroupDecisions.some((d) => payload.reviewGroupDecisions[d]?.type === 'ACCEPTED');

const rejectedSectionExists = (payload: UNAVariationReviewRequestTaskPayload) =>
  staticVariationGroupDecisions.some((d) => payload.reviewGroupDecisions[d]?.type === 'REJECTED');

const acceptedFacilityDecisionExists = (payload: UNAVariationReviewRequestTaskPayload) =>
  Object.keys(payload.facilitiesReviewGroupDecisions).some(
    (k) => payload.facilitiesReviewGroupDecisions[k]?.type === 'ACCEPTED',
  );

const rejectedFacilityDecisionExists = (payload: UNAVariationReviewRequestTaskPayload) =>
  Object.keys(payload.facilitiesReviewGroupDecisions).some(
    (k) => payload.facilitiesReviewGroupDecisions[k]?.type === 'REJECTED',
  );

const rejectedAndNew = (f: FacilityWithDecision) => !(f.decisionType === 'REJECTED' && f.status === 'NEW');

const rejectedToOriginal =
  (originalUnderlyingAgreementContainer: UnderlyingAgreementContainer) => (facility: FacilityWithDecision) =>
    facility.decisionType === 'REJECTED'
      ? originalUnderlyingAgreementContainer.underlyingAgreement.facilities.find(
          (f) => f.facilityId === facility.facilityId,
        )
      : toFacility(facility);

const toFacility = (f: FacilityWithDecision) =>
  produce(f, (facility) => {
    delete facility.decisionType;
  });

export const reviewSectionsCompleted = (payload: UNAVariationReviewRequestTaskPayload) => {
  if (!payload.reviewSectionsCompleted[VARIATION_DETAILS_SUBTASK]) return false;

  const hasUndecidedSection = Object.keys(payload.reviewSectionsCompleted)
    .filter((k) => k !== OVERALL_DECISION_SUBTASK)
    .some((k) => payload.reviewSectionsCompleted[k] === TaskItemStatus.UNDECIDED);

  if (hasUndecidedSection) return false;

  const groupDecisionsCompleted = staticVariationGroupDecisions.every((s) => payload.reviewGroupDecisions[s]);
  if (!groupDecisionsCompleted) return false;

  return payload.underlyingAgreement.facilities.every((f) => payload.facilitiesReviewGroupDecisions[f.facilityId]);
};

export const activeFacilityExists = (payload: UNAVariationReviewRequestTaskPayload) => {
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

  return activeExcluded || activeLive || activeNew;
};

export const canAcceptVariationPayload = (payload: UNAVariationReviewRequestTaskPayload) =>
  (acceptedSectionExists(payload) || acceptedFacilityDecisionExists(payload)) && activeFacilityExists(payload);

export const canRejectVariationPayload = (payload: UNAVariationReviewRequestTaskPayload) =>
  rejectedSectionExists(payload) || rejectedFacilityDecisionExists(payload) || !activeFacilityExists(payload);

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
  } = payload;

  const proposed: UnderlyingAgreementVariationPayload = {
    underlyingAgreementVariationDetails: underlyingAgreement.underlyingAgreementVariationDetails,
    underlyingAgreementTargetUnitDetails: underlyingAgreement.underlyingAgreementTargetUnitDetails,
    facilities: [],
    targetPeriod5Details: underlyingAgreement.targetPeriod5Details,
    targetPeriod6Details: underlyingAgreement.targetPeriod6Details,
    authorisationAndAdditionalEvidence: underlyingAgreement.authorisationAndAdditionalEvidence,
  };

  if (reviewGroupDecisions['TARGET_UNIT_DETAILS'].type === 'REJECTED')
    proposed.underlyingAgreementTargetUnitDetails = transform(accountReferenceData);

  Object.entries(reviewGroupDecisions)
    .filter((rg) => rg[0] !== 'TARGET_UNIT_DETAILS' && rg[0] !== 'VARIATION_DETAILS')
    .forEach((rg) => {
      if (rg[1].type === 'REJECTED')
        proposed[StaticVariationGroupDecisionsEnum[rg[0]]] =
          originalUnderlyingAgreementContainer.underlyingAgreement[StaticVariationGroupDecisionsEnum[rg[0]]];
    });

  proposed.facilities = Object.keys(facilitiesReviewGroupDecisions)
    .map((k) => ({
      decisionType: facilitiesReviewGroupDecisions[k].type,
      ...underlyingAgreement.facilities.find((f) => f.facilityId === k),
    }))
    .filter(rejectedAndNew)
    .map(rejectedToOriginal(originalUnderlyingAgreementContainer));

  return proposed;
};
