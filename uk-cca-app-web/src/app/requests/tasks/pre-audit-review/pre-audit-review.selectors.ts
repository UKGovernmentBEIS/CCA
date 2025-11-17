import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { PreAuditReviewDetails, PreAuditReviewSubmitRequestTaskPayload } from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, PreAuditReviewSubmitRequestTaskPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as PreAuditReviewSubmitRequestTaskPayload,
);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.sectionsCompleted,
);

const selectFacilityAuditAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.facilityAuditAttachments);

const selectPreAuditReviewDetails: StateSelector<RequestTaskState, PreAuditReviewDetails> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.preAuditReviewDetails,
);

export const preAuditReviewQuery = {
  selectPayload,
  selectSectionsCompleted,
  selectFacilityAuditAttachments,
  selectPreAuditReviewDetails,
};
