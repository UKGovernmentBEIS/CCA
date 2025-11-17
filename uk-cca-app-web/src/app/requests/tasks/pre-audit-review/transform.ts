import {
  PreAuditReviewSubmitRequestTaskPayload,
  PreAuditReviewSubmitSaveRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'cca-api';

type UnaRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: PreAuditReviewSubmitSaveRequestTaskActionPayload;
};

type PreAuditReviewSubmitDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: {
    payloadType: 'EMPTY_PAYLOAD';
  };
};

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: PreAuditReviewSubmitRequestTaskPayload,
  sectionsCompleted: Record<string, string>,
): UnaRequestTaskActionProcessDTO {
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
