import { createDescendingSelector } from '@netz/common/store';
import { staticGroupDecisions, UNAReviewRequestTaskPayload, underlyingAgreementQuery } from '@requests/common';

const selectCanAccept = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload) =>
    staticGroupDecisions.every((d) => payload.reviewGroupDecisions[d].type === 'ACCEPTED') &&
    Object.keys(payload.facilitiesReviewGroupDecisions).some(
      (k) => payload.facilitiesReviewGroupDecisions[k].type === 'ACCEPTED',
    ),
);

const selectCanReject = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload) =>
    staticGroupDecisions.some((d) => payload.reviewGroupDecisions[d].type === 'REJECTED') ||
    Object.keys(payload.facilitiesReviewGroupDecisions).some(
      (k) => payload.facilitiesReviewGroupDecisions[k].type === 'REJECTED',
    ),
);

export const underlyingAgreementReviewQuery = {
  selectCanAccept,
  selectCanReject,
};
