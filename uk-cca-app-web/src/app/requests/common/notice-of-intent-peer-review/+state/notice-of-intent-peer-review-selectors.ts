import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  NonComplianceNoticeOfIntent,
  NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload,
  NonComplianceNoticeOfIntentSubmitRequestTaskPayload,
} from 'cca-api';

type NoticeOfIntentPeerReviewPayload =
  NonComplianceNoticeOfIntentSubmitRequestTaskPayload | NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload;

const selectPayload: StateSelector<RequestTaskState, NoticeOfIntentPeerReviewPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as NoticeOfIntentPeerReviewPayload,
);

const selectNoticeOfIntent: StateSelector<RequestTaskState, NonComplianceNoticeOfIntent> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.noticeOfIntent,
);

const selectNonComplianceAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.nonComplianceAttachments);

export const noticeOfIntentPeerReviewQuery = {
  selectPayload,
  selectNoticeOfIntent,
  selectNonComplianceAttachments,
};
