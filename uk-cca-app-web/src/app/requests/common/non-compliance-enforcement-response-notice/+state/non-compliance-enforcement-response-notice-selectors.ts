import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { NonComplianceEnforcementResponseNotice } from 'cca-api';

import { NonComplianceEnforcementResponseNoticePayload } from '../types';

const selectPayload: StateSelector<RequestTaskState, NonComplianceEnforcementResponseNoticePayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as NonComplianceEnforcementResponseNoticePayload,
  );

const selectEnforcementResponseNotice: StateSelector<RequestTaskState, NonComplianceEnforcementResponseNotice> =
  createDescendingSelector(selectPayload, (payload) => payload?.enforcementResponseNotice);

const selectNonComplianceAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.nonComplianceAttachments);

const selectIsPenaltyReissue: StateSelector<RequestTaskState, boolean> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.penaltyReissue === true,
);

export const nonComplianceEnforcementResponseNoticeQuery = {
  selectPayload,
  selectEnforcementResponseNotice,
  selectNonComplianceAttachments,
  selectIsPenaltyReissue,
};
