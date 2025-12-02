import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import { AuditDetailsAndCorrectiveActions, AuditDetailsCorrectiveActionsSubmittedRequestActionPayload } from 'cca-api';

const selectPayload: StateSelector<RequestActionState, AuditDetailsCorrectiveActionsSubmittedRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (payload) => payload as AuditDetailsCorrectiveActionsSubmittedRequestActionPayload,
  );

const selectFacilityAuditAttachments: StateSelector<
  RequestActionState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.facilityAuditAttachments);

const selectAuditDetailsAndCorrectiveActions: StateSelector<RequestActionState, AuditDetailsAndCorrectiveActions> =
  createDescendingSelector(selectPayload, (payload) => payload?.auditDetailsAndCorrectiveActions);

export const detailsCorrectiveActionsCompletedQuery = {
  selectPayload,
  selectFacilityAuditAttachments,
  selectAuditDetailsAndCorrectiveActions,
};
