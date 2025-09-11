import {
  RequestTaskActionProcessDTO,
  UnderlyingAgreementTargetUnitDetails,
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationApplySaveTargetUnitDetails,
  UnderlyingAgreementVariationPayload,
  UnderlyingAgreementVariationSaveRequestTaskActionPayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { FacilityReviewProps, ReviewProps } from './utils';

// Type definitions for DTOs
type UnaVariationRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementVariationSaveRequestTaskActionPayload;
};

type UnaVariationSubmitDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: {
    payloadType: 'EMPTY_PAYLOAD';
  };
};

// Main DTO creation functions
export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: UnderlyingAgreementVariationApplySavePayload,
  sectionsCompleted: Record<string, string>,
  reviewProps: ReviewProps & FacilityReviewProps,
): UnaVariationRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SAVE_PAYLOAD',
      underlyingAgreement: payload,
      sectionsCompleted,
      ...reviewProps,
    },
  };
}

export function createSubmitActionDTO(requestTaskId: number): UnaVariationSubmitDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'EMPTY_PAYLOAD',
    },
  };
}

// Payload transformation functions
export function toUnderlyingAgreementVariationSavePayload(
  payload: UnderlyingAgreementVariationSubmitRequestTaskPayload,
): UnderlyingAgreementVariationApplySavePayload {
  if (!payload.underlyingAgreement) throw new Error('Underlying agreement payload is missing');
  return transformUnderlyingAgreement(payload.underlyingAgreement);
}

function transformUnderlyingAgreement(
  underlyingAgreement: UnderlyingAgreementVariationPayload,
): UnderlyingAgreementVariationApplySavePayload {
  return {
    underlyingAgreementVariationDetails: underlyingAgreement.underlyingAgreementVariationDetails,
    underlyingAgreementTargetUnitDetails: transformTargetUnitDetails(
      underlyingAgreement.underlyingAgreementTargetUnitDetails,
    ),
    facilities: underlyingAgreement.facilities, // No transformation needed - already Facility[]
    targetPeriod5Details: underlyingAgreement.targetPeriod5Details,
    targetPeriod6Details: underlyingAgreement.targetPeriod6Details,
    authorisationAndAdditionalEvidence: underlyingAgreement.authorisationAndAdditionalEvidence,
  };
}

function transformTargetUnitDetails(
  details: UnderlyingAgreementTargetUnitDetails,
): UnderlyingAgreementVariationApplySaveTargetUnitDetails {
  return {
    operatorName: details.operatorName,
    operatorAddress: details.operatorAddress,
    responsiblePersonDetails: details.responsiblePersonDetails,
  };
}
