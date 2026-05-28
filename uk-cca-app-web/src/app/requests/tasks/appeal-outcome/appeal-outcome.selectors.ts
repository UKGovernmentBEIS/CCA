import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { AppealOutcome, AppealOutcomeRequestTaskPayload } from './types';

const selectPayload: StateSelector<RequestTaskState, AppealOutcomeRequestTaskPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as AppealOutcomeRequestTaskPayload,
);

const selectAppealOutcome: StateSelector<RequestTaskState, AppealOutcome> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.appealOutcome,
);

const selectNonComplianceAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.nonComplianceAttachments);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.sectionsCompleted,
);

export const appealOutcomeQuery = {
  selectPayload,
  selectAppealOutcome,
  selectNonComplianceAttachments,
  selectSectionsCompleted,
};
