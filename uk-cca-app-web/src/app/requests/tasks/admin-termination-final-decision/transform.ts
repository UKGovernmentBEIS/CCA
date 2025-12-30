import { AdminTerminationFinalDecisionSaveRequestTaskActionPayload, CcaDecisionNotification } from 'cca-api';

import {
  AdminTerminationFinalDecisionNotifyDto,
  AdminTerminationFinalDecisionRequestTaskActionProcessDTO,
} from './types';

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: AdminTerminationFinalDecisionSaveRequestTaskActionPayload,
  sectionsCompleted: Record<string, string>,
): AdminTerminationFinalDecisionRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'ADMIN_TERMINATION_FINAL_DECISION_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'ADMIN_TERMINATION_FINAL_DECISION_SAVE_PAYLOAD',
      adminTerminationFinalDecisionReasonDetails: payload?.adminTerminationFinalDecisionReasonDetails,
      sectionsCompleted,
    },
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: CcaDecisionNotification,
): AdminTerminationFinalDecisionNotifyDto {
  return {
    requestTaskId,
    requestTaskActionType: 'ADMIN_TERMINATION_FINAL_DECISION_NOTIFY_OPERATOR_FOR_DECISION',
    requestTaskActionPayload: {
      payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      decisionNotification,
    },
  };
}
