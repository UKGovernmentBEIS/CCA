import {
  DecisionNotification,
  NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload,
  NotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'cca-api';

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload,
  sectionsCompleted: Record<string, string>,
): RequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_PAYLOAD',
      enforcementResponseNotice: payload?.enforcementResponseNotice,
      sectionsCompleted,
    } as NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload,
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: DecisionNotification,
): RequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_NOTIFY_OPERATOR',
    requestTaskActionPayload: {
      payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      decisionNotification,
    } as NotifyOperatorForDecisionRequestTaskActionPayload,
  };
}
