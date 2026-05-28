import { CcaPeerReviewDecision, NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload } from 'cca-api';

export const UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK = 'uploadEnforcementResponseNotice';

export type NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload =
  NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload & {
    decision?: CcaPeerReviewDecision;
    peerReviewAttachments?: Record<string, string>;
  };

export type NonComplianceEnforcementResponseNoticePayload =
  | NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload
  | NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload;
