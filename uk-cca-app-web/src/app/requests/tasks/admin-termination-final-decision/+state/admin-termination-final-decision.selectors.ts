import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { AdminTerminationFinalDecisionReasonDetails, AdminTerminationFinalDecisionRequestTaskPayload } from 'cca-api';

const selectAdminTerminationFinalDecisionPayload: StateSelector<
  RequestTaskState,
  AdminTerminationFinalDecisionRequestTaskPayload
> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as AdminTerminationFinalDecisionRequestTaskPayload,
);

const selectAdminTerminationFinalDecisionReasonDetails: StateSelector<
  RequestTaskState,
  AdminTerminationFinalDecisionReasonDetails
> = createDescendingSelector(
  selectAdminTerminationFinalDecisionPayload,
  (payload) => payload?.adminTerminationFinalDecisionReasonDetails,
);

const selectAdminTerminationFinalDecisionAttachments: StateSelector<RequestTaskState, { [key: string]: string }> =
  createDescendingSelector(
    selectAdminTerminationFinalDecisionPayload,
    (payload) => payload?.adminTerminationAttachments,
  );

const selectAdminTerminationFinalDecisionSectionsCompleted: StateSelector<RequestTaskState, { [key: string]: string }> =
  createDescendingSelector(selectAdminTerminationFinalDecisionPayload, (payload) => payload?.sectionsCompleted);

export const AdminTerminationFinalDecisionQuery = {
  selectAdminTerminationFinalDecisionPayload,
  selectAdminTerminationFinalDecisionReasonDetails,
  selectAdminTerminationFinalDecisionAttachments,
  selectAdminTerminationFinalDecisionSectionsCompleted,
};
