import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import { AuditTrackCorrectiveActions, AuditTrackCorrectiveActionsSubmittedRequestActionPayload } from 'cca-api';

const selectActionPayload: StateSelector<RequestActionState, AuditTrackCorrectiveActionsSubmittedRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (payload) => payload as AuditTrackCorrectiveActionsSubmittedRequestActionPayload,
  );

const selectFacilityAuditAttachments: StateSelector<
  RequestActionState,
  Record<string, string>
> = createDescendingSelector(selectActionPayload, (payload) => payload?.facilityAuditAttachments);

const selectAuditTrackCorrectiveActions: StateSelector<RequestActionState, AuditTrackCorrectiveActions> =
  createDescendingSelector(selectActionPayload, (payload) => payload?.auditTrackCorrectiveActions);

export const trackCorrectiveActionsCompletedQuery = {
  selectActionPayload,
  selectFacilityAuditAttachments,
  selectAuditTrackCorrectiveActions,
};
