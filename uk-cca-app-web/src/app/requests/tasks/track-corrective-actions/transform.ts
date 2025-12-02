import {
  AuditDetailsAndCorrectiveActionsSubmitDTO,
  TrackCorreciveActionsRequestTaskActionProcessDTO,
} from '@requests/common';

import {
  AuditTrackCorrectiveActionsRequestTaskPayload,
  AuditTrackCorrectiveActionsSaveRequestTaskActionPayload,
} from 'cca-api';

export function toSaveRequestTaskPayload(
  payload: AuditTrackCorrectiveActionsRequestTaskPayload,
  actionId: string,
): AuditTrackCorrectiveActionsSaveRequestTaskActionPayload {
  const action = payload?.auditTrackCorrectiveActions?.correctiveActionResponses[actionId];

  return {
    actionTitle: action?.title,
    correctiveActionFollowUpResponse: {
      isActionCarriedOut: action?.isActionCarriedOut,
      actionCarriedOutDate: action?.actionCarriedOutDate,
      comments: action?.comments,
      evidenceFiles: action?.evidenceFiles,
    },
  };
}

export function createSaveRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: AuditTrackCorrectiveActionsSaveRequestTaskActionPayload,
  sectionsCompleted: Record<string, string>,
): TrackCorreciveActionsRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SAVE_RESPONSE',
    requestTaskActionPayload: {
      payloadType: 'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SAVE_PAYLOAD',
      sectionsCompleted,
      correctiveActionFollowUpResponse: payload?.correctiveActionFollowUpResponse,
      actionTitle: payload?.actionTitle,
    },
  };
}

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: AuditTrackCorrectiveActionsSaveRequestTaskActionPayload,
  sectionsCompleted: Record<string, string>,
): TrackCorreciveActionsRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMIT_RESPONSE',
    requestTaskActionPayload: {
      payloadType: 'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD',
      sectionsCompleted,
      actionTitle: payload?.actionTitle,
    },
  };
}

export function createSubmitActionDTO(requestTaskId: number): AuditDetailsAndCorrectiveActionsSubmitDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_COMPLETE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'EMPTY_PAYLOAD',
    },
  };
}
