import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  AdminTerminationPeerReviewRequestTaskPayload,
  AdminTerminationReasonDetails,
  AdminTerminationSubmitRequestTaskPayload,
} from 'cca-api';

type AdminTerminationPayloadWithReasonDetails =
  | AdminTerminationSubmitRequestTaskPayload
  | AdminTerminationPeerReviewRequestTaskPayload;

const selectAdminTerminationPayloadWithReasonDetails: StateSelector<
  RequestTaskState,
  AdminTerminationPayloadWithReasonDetails
> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as AdminTerminationPayloadWithReasonDetails,
);

const selectPeerReviewAdminTerminationReasonDetails: StateSelector<RequestTaskState, AdminTerminationReasonDetails> =
  createDescendingSelector(
    selectAdminTerminationPayloadWithReasonDetails,
    (payload) => payload.adminTerminationReasonDetails,
  );

const selectAttachments: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectAdminTerminationPayloadWithReasonDetails,
  (payload) => payload?.adminTerminationAttachments,
);

export const adminTerminationPeerReviewQuery = {
  selectAdminTerminationPayloadWithReasonDetails,
  selectPeerReviewAdminTerminationReasonDetails,
  selectAttachments,
};
