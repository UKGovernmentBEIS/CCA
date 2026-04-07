import {
  DecisionNotification,
  NonComplianceNoticeOfIntentSubmitSaveRequestTaskActionPayload,
  NotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'cca-api';

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: NonComplianceNoticeOfIntentSubmitSaveRequestTaskActionPayload,
  sectionsCompleted: Record<string, string>,
): RequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_PAYLOAD',
      noticeOfIntent: payload?.noticeOfIntent,
      sectionsCompleted,
    } as NonComplianceNoticeOfIntentSubmitSaveRequestTaskActionPayload,
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: DecisionNotification,
): RequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR',
    requestTaskActionPayload: {
      payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      decisionNotification,
    } as NotifyOperatorForDecisionRequestTaskActionPayload,
  };
}
