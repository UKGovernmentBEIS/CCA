import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import { TargetUnitAccountCreationSubmittedRequestActionPayload } from 'cca-api';

const selectPayload: StateSelector<RequestActionState, TargetUnitAccountCreationSubmittedRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (actionPayload) => actionPayload as TargetUnitAccountCreationSubmittedRequestActionPayload,
  );

export const targetUnitCreationTimelineQuery = {
  selectPayload,
};
