import { AdminTerminationSaveRequestTaskActionPayload, CcaDecisionNotification } from 'cca-api';

import { AdminTerminationNotifyDto, AdminTerminationRequestTaskActionProcessDTO } from './types';

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: AdminTerminationSaveRequestTaskActionPayload,
  sectionsCompleted: Record<string, string>,
): AdminTerminationRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'ADMIN_TERMINATION_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'ADMIN_TERMINATION_SAVE_PAYLOAD',
      adminTerminationReasonDetails: payload?.adminTerminationReasonDetails,
      sectionsCompleted,
    },
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: CcaDecisionNotification,
): AdminTerminationNotifyDto {
  return {
    requestTaskId,
    requestTaskActionType: 'ADMIN_TERMINATION_NOTIFY_OPERATOR_FOR_DECISION',
    requestTaskActionPayload: {
      payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      decisionNotification,
    },
  };
}
