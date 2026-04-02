import { UNAVariationRegulatorLedRequestTaskPayload } from '@requests/common';

import {
  CcaDecisionNotification,
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  UnderlyingAgreementVariationRegulatorLedSavePayload,
  UnderlyingAgreementVariationRegulatorLedSaveRequestTaskActionPayload,
  VariationRegulatorLedDetermination,
} from 'cca-api';

type UnaVariationRegulatorLedRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementVariationRegulatorLedSaveRequestTaskActionPayload;
};

type UnaVariationRegulatorLedNotifyDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: CcaNotifyOperatorForDecisionRequestTaskActionPayload;
};

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: UnderlyingAgreementVariationRegulatorLedSavePayload,
  sectionsCompleted: Record<string, string>,
  determination?: VariationRegulatorLedDetermination,
): UnaVariationRegulatorLedRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SAVE_PAYLOAD',
      underlyingAgreement: payload,
      sectionsCompleted,
      determination,
    },
  };
}

export function toUnAVariationRegulatorLedSavePayload(
  payload: UNAVariationRegulatorLedRequestTaskPayload,
): UnderlyingAgreementVariationRegulatorLedSavePayload {
  if (!payload) throw new Error('Regulator-led payload is missing');
  return {
    underlyingAgreementVariationDetails: payload.underlyingAgreement.underlyingAgreementVariationDetails,
    underlyingAgreementTargetUnitDetails: payload.underlyingAgreement.underlyingAgreementTargetUnitDetails,
    facilities: payload.underlyingAgreement.facilities,
    targetPeriod5Details: payload.underlyingAgreement.targetPeriod5Details,
    targetPeriod6Details: payload.underlyingAgreement.targetPeriod6Details,
    authorisationAndAdditionalEvidence: payload.underlyingAgreement.authorisationAndAdditionalEvidence,
    facilityChargeStartDateMap: payload.facilityChargeStartDateMap,
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: CcaDecisionNotification,
): UnaVariationRegulatorLedNotifyDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_NOTIFY_OPERATOR_FOR_DECISION',
    requestTaskActionPayload: {
      payloadType: 'CCA_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      decisionNotification,
    },
  };
}
