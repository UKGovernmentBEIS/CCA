import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { AdminTerminationWithdrawReasonDetails, AdminTerminationWithdrawRequestTaskPayload } from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, AdminTerminationWithdrawRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as AdminTerminationWithdrawRequestTaskPayload,
  );

const selectReasonDetails: StateSelector<RequestTaskState, AdminTerminationWithdrawReasonDetails> =
  createDescendingSelector(selectPayload, (payload) => payload.adminTerminationWithdrawReasonDetails);

const selectAttachments: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.adminTerminationAttachments,
);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.sectionsCompleted,
);

export const adminTerminationWithdrawQuery = {
  selectPayload,
  selectReasonDetails,
  selectAttachments,
  selectSectionsCompleted,
};
