import { PreAuditReviewSubmitRequestTaskPayload } from 'cca-api';

import { PreAuditReviewRequestTaskActionProcessDTO, PreAuditReviewSubmitDTO } from './types';

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: PreAuditReviewSubmitRequestTaskPayload,
  sectionsCompleted: Record<string, string>,
): PreAuditReviewRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'FACILITY_AUDIT_PRE_AUDIT_REVIEW_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'FACILITY_AUDIT_PRE_AUDIT_REVIEW_SAVE_PAYLOAD',
      preAuditReviewDetails: payload?.preAuditReviewDetails,
      sectionsCompleted,
    },
  };
}

export function createSubmitActionDTO(requestTaskId: number): PreAuditReviewSubmitDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMIT_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'EMPTY_PAYLOAD',
    },
  };
}
