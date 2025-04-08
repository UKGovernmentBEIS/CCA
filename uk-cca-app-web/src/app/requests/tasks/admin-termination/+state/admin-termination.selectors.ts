import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { AdminTerminationReasonDetails, AdminTerminationSubmitRequestTaskPayload } from 'cca-api';

const selectAdminTerminationPayload: StateSelector<RequestTaskState, AdminTerminationSubmitRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as AdminTerminationSubmitRequestTaskPayload,
  );

const selectAdminTerminationReasonDetails: StateSelector<RequestTaskState, AdminTerminationReasonDetails> =
  createDescendingSelector(selectAdminTerminationPayload, (payload) => payload.adminTerminationReasonDetails);

const selectAdminTerminationSubmitAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectAdminTerminationPayload, (payload) => payload.adminTerminationAttachments);

const selectAdminTerminationSectionsCompleted: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectAdminTerminationPayload, (payload) => payload.sectionsCompleted);

export const AdminTerminationQuery = {
  selectAdminTerminationPayload,
  selectAdminTerminationReasonDetails,
  selectAdminTerminationSubmitAttachments,
  selectAdminTerminationSectionsCompleted,
};
