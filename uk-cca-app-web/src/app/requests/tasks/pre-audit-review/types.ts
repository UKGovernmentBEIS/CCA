import { PreAuditReviewSubmitSaveRequestTaskActionPayload, RequestTaskActionProcessDTO } from 'cca-api';

export const PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK = 'preAuditReviewAuditReason';
export const PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_SUBTASK = 'preAuditReviewRequestedDocuments';
export const PRE_AUDIT_REVIEW_DETERMINATION_SUBTASK = 'preAuditReviewDetermination';

export const PRE_AUDIT_REVIEW_UPLOAD_SECTION_ATTACHMENT_TYPE = {
  PRE_AUDIT_REVIEW_SUBMIT: 'FACILITY_AUDIT_UPLOAD_ATTACHMENT',
};

export type PreAuditReviewRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: PreAuditReviewSubmitSaveRequestTaskActionPayload;
};

export type PreAuditReviewSubmitDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: {
    payloadType: 'EMPTY_PAYLOAD';
  };
};
