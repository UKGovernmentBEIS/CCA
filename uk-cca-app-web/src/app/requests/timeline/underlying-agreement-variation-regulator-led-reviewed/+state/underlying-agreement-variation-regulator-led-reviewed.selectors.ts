import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import { CcaPeerReviewDecisionSubmittedRequestActionPayload } from 'cca-api';

const selectPayload: StateSelector<RequestActionState, CcaPeerReviewDecisionSubmittedRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (actionPayload) => actionPayload as CcaPeerReviewDecisionSubmittedRequestActionPayload,
  );

const selectDecision: StateSelector<RequestActionState, any> = createDescendingSelector(
  selectPayload,
  (payload) => payload.decision,
);

const selectPeerReviewAttachments: StateSelector<RequestActionState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.peerReviewAttachments,
);

export const unaVariationRegulatorLedReviewedQuery = {
  selectPayload,
  selectDecision,
  selectPeerReviewAttachments,
};
