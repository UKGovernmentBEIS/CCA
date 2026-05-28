import {
  DecisionNotification,
  NotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'cca-api';

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: DecisionNotification,
): RequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'NON_COMPLIANCE_CONCLUSION_NOTIFY_OPERATOR',
    requestTaskActionPayload: {
      payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      decisionNotification,
    } as NotifyOperatorForDecisionRequestTaskActionPayload,
  };
}
