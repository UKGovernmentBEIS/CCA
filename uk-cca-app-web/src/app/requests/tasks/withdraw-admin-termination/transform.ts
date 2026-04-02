import { AdminTerminationWithdrawSaveRequestTaskActionPayload, CcaDecisionNotification } from 'cca-api';

import { WithdrawAdminTerminationNotifyDto, WithdrawAdminTerminationRequestTaskActionProcessDTO } from './types';

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: AdminTerminationWithdrawSaveRequestTaskActionPayload,
  sectionsCompleted: Record<string, string>,
): WithdrawAdminTerminationRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'ADMIN_TERMINATION_WITHDRAW_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'ADMIN_TERMINATION_WITHDRAW_SAVE_PAYLOAD',
      adminTerminationWithdrawReasonDetails: payload?.adminTerminationWithdrawReasonDetails,
      sectionsCompleted,
    },
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: CcaDecisionNotification,
): WithdrawAdminTerminationNotifyDto {
  return {
    requestTaskId,
    requestTaskActionType: 'ADMIN_TERMINATION_WITHDRAW_NOTIFY_OPERATOR_FOR_DECISION',
    requestTaskActionPayload: {
      payloadType: 'CCA_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      decisionNotification,
    },
  };
}
