import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import { CcaPeerReviewDecisionSubmittedRequestActionPayload } from 'cca-api';

export type PeerReviewDecisionActionPayload = CcaPeerReviewDecisionSubmittedRequestActionPayload;

const selectPayload: StateSelector<RequestActionState, CcaPeerReviewDecisionSubmittedRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (actionPayload) => actionPayload as PeerReviewDecisionActionPayload,
  );

export const adminTerminationPeerReviewQuery = {
  selectPayload,
};
