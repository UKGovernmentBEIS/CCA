import { CcaDecisionNotification, UnderlyingAgreementActivationSaveRequestTaskActionPayload } from 'cca-api';

import { UnAActivationNotifyDto, UnAActivationRequestTaskActionProcessDTO } from './types';

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: UnderlyingAgreementActivationSaveRequestTaskActionPayload,
  sectionsCompleted: Record<string, string>,
): UnAActivationRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_ACTIVATION_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_ACTIVATION_SAVE_PAYLOAD',
      underlyingAgreementActivationDetails: payload?.underlyingAgreementActivationDetails,
      sectionsCompleted,
    },
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: CcaDecisionNotification,
): UnAActivationNotifyDto {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_ACTIVATION_NOTIFY_OPERATOR_FOR_DECISION',
    requestTaskActionPayload: {
      payloadType: 'CCA_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      decisionNotification,
    },
  };
}
