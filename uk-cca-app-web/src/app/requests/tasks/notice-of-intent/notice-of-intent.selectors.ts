import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { NonComplianceNoticeOfIntentSubmitRequestTaskPayload, NoticeOfIntent } from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, NonComplianceNoticeOfIntentSubmitRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as NonComplianceNoticeOfIntentSubmitRequestTaskPayload,
  );

const selectNoticeOfIntent: StateSelector<RequestTaskState, NoticeOfIntent> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.noticeOfIntent,
);

const selectNonComplianceAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.nonComplianceAttachments);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.sectionsCompleted,
);

export const noticeOfIntentQuery = {
  selectPayload,
  selectNoticeOfIntent,
  selectNonComplianceAttachments,
  selectSectionsCompleted,
};
