import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { AuditDetailsAndCorrectiveActions, AuditDetailsCorrectiveActionsSubmitRequestTaskPayload } from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, AuditDetailsCorrectiveActionsSubmitRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as AuditDetailsCorrectiveActionsSubmitRequestTaskPayload,
  );

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.sectionsCompleted,
);

const selectFacilityAuditAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.facilityAuditAttachments);

const selectAuditDetailsAndCorrectiveActions: StateSelector<RequestTaskState, AuditDetailsAndCorrectiveActions> =
  createDescendingSelector(selectPayload, (payload) => payload?.auditDetailsAndCorrectiveActions);

export const auditDetailsCorrectiveActionsQuery = {
  selectPayload,
  selectSectionsCompleted,
  selectFacilityAuditAttachments,
  selectAuditDetailsAndCorrectiveActions,
};
