import { CcaDecisionNotification, UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload } from 'cca-api';

import { UnAVariationActivationNotifyDto, UnAVariationActivationRequestTaskActionProcessDTO } from './types';

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload,
  sectionsCompleted: Record<string, string>,
): UnAVariationActivationRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_SAVE_PAYLOAD',
      underlyingAgreementActivationDetails: payload?.underlyingAgreementActivationDetails,
      sectionsCompleted,
    },
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: CcaDecisionNotification,
): UnAVariationActivationNotifyDto {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_NOTIFY_OPERATOR_FOR_DECISION',
    requestTaskActionPayload: {
      payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      decisionNotification,
    },
  };
}
