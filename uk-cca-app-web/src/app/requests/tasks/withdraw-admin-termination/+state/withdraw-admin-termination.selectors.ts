import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { AdminTerminationWithdrawReasonDetails, AdminTerminationWithdrawRequestTaskPayload } from 'cca-api';

const selectWithdrawAdminTerminationPayload: StateSelector<
  RequestTaskState,
  AdminTerminationWithdrawRequestTaskPayload
> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as AdminTerminationWithdrawRequestTaskPayload,
);

const selectWithdrawAdminTerminationReasonDetails: StateSelector<
  RequestTaskState,
  AdminTerminationWithdrawReasonDetails
> = createDescendingSelector(
  selectWithdrawAdminTerminationPayload,
  (payload) => payload.adminTerminationWithdrawReasonDetails,
);

const selectWithdrawAdminTerminationAttachments: StateSelector<RequestTaskState, { [key: string]: string }> =
  createDescendingSelector(selectWithdrawAdminTerminationPayload, (payload) => payload.adminTerminationAttachments);

const selectWithdrawAdminTerminationSectionsCompleted: StateSelector<RequestTaskState, { [key: string]: string }> =
  createDescendingSelector(selectWithdrawAdminTerminationPayload, (payload) => payload.sectionsCompleted);

export const AdminTerminationWithdrawQuery = {
  selectWithdrawAdminTerminationPayload,
  selectWithdrawAdminTerminationReasonDetails,
  selectWithdrawAdminTerminationAttachments,
  selectWithdrawAdminTerminationSectionsCompleted,
};
