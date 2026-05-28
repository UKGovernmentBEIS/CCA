import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  NonComplianceEnforcementResponseNotice,
  NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload,
} from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload,
  );

const selectEnforcementResponseNotice: StateSelector<RequestTaskState, NonComplianceEnforcementResponseNotice> =
  createDescendingSelector(selectPayload, (payload) => payload?.enforcementResponseNotice);

const selectNonComplianceAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.nonComplianceAttachments);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.sectionsCompleted,
);

const selectIsPenaltyReissue: StateSelector<RequestTaskState, boolean> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.penaltyReissue === true,
);

export const enforcementResponseNoticeQuery = {
  selectPayload,
  selectEnforcementResponseNotice,
  selectNonComplianceAttachments,
  selectSectionsCompleted,
  selectIsPenaltyReissue,
};
