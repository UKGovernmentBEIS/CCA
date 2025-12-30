import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { AdminTerminationFinalDecisionReasonDetails, AdminTerminationFinalDecisionRequestTaskPayload } from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, AdminTerminationFinalDecisionRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as AdminTerminationFinalDecisionRequestTaskPayload,
  );

const selectReasonDetails: StateSelector<RequestTaskState, AdminTerminationFinalDecisionReasonDetails> =
  createDescendingSelector(selectPayload, (payload) => payload?.adminTerminationFinalDecisionReasonDetails);

const selectAttachments: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.adminTerminationAttachments,
);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.sectionsCompleted,
);

export const adminTerminationFinalDecisionQuery = {
  selectPayload,
  selectReasonDetails,
  selectAttachments,
  selectSectionsCompleted,
};
