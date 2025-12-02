import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import { PreAuditReviewDetails, PreAuditReviewSubmittedRequestActionPayload } from 'cca-api';

const selectPayload: StateSelector<RequestActionState, PreAuditReviewSubmittedRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (payload) => payload as PreAuditReviewSubmittedRequestActionPayload,
  );

const selectFacilityAuditAttachments: StateSelector<
  RequestActionState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.facilityAuditAttachments);

const selectPreAuditReviewDetails: StateSelector<RequestActionState, PreAuditReviewDetails> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.preAuditReviewDetails,
);

export const preAuditReviewCompletedQuery = {
  selectPayload,
  selectFacilityAuditAttachments,
  selectPreAuditReviewDetails,
};
