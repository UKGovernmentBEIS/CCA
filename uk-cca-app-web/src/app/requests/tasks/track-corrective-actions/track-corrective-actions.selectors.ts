import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { AuditTrackCorrectiveActions, AuditTrackCorrectiveActionsRequestTaskPayload } from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, AuditTrackCorrectiveActionsRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as AuditTrackCorrectiveActionsRequestTaskPayload,
  );

const selectRespondedActions: StateSelector<RequestTaskState, string[]> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.respondedActions,
);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.sectionsCompleted,
);

const selectFacilityAuditAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.facilityAuditAttachments);

const selectAuditTrackCorrectiveActions: StateSelector<RequestTaskState, AuditTrackCorrectiveActions> =
  createDescendingSelector(selectPayload, (payload) => payload?.auditTrackCorrectiveActions);

export const trackCorrectiveActionsQuery = {
  selectPayload,
  selectSectionsCompleted,
  selectFacilityAuditAttachments,
  selectAuditTrackCorrectiveActions,
  selectRespondedActions,
};
