import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { AdminTerminationReasonDetails, AdminTerminationSubmitRequestTaskPayload } from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, AdminTerminationSubmitRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as AdminTerminationSubmitRequestTaskPayload,
  );

const selectReasonDetails: StateSelector<RequestTaskState, AdminTerminationReasonDetails> = createDescendingSelector(
  selectPayload,
  (payload) => payload.adminTerminationReasonDetails,
);

const selectSubmitAttachments: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.adminTerminationAttachments,
);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.sectionsCompleted,
);

export const adminTerminationQuery = {
  selectPayload,
  selectReasonDetails,
  selectSubmitAttachments,
  selectSectionsCompleted,
};
