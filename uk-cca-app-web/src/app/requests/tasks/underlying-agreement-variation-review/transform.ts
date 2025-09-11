import {
  CcaDecisionNotification,
  Determination,
  RequestTaskActionProcessDTO,
  UnderlyingAgreementReviewDecision,
  UnderlyingAgreementVariationFacilityReviewDecision,
  UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationPayload,
  UnderlyingAgreementVariationReviewRequestTaskPayload,
  UnderlyingAgreementVariationReviewSavePayload,
  UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload,
} from 'cca-api';

// Type definitions for DTOs
type UnaVariationReviewSaveActionDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload;
};

type UnaVariationReviewSaveDecisionDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload;
};

type UnaVariationReviewSaveFacilityDecisionDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
};

type UnaVariationReviewSaveDeterminationDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload;
};

type UnaVariationReviewNotifyDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload;
};

// Main DTO creation functions
export function createSaveActionDTO(
  requestTaskId: number,
  underlyingAgreement: UnderlyingAgreementVariationReviewSavePayload,
  requestTaskProps: {
    sectionsCompleted?: Record<string, string>;
    reviewSectionsCompleted: Record<string, string>;
    determination: Determination;
  },
): UnaVariationReviewSaveActionDTO {
  const { determination, reviewSectionsCompleted, sectionsCompleted } = requestTaskProps;

  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD',
      underlyingAgreement,
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
    },
  };
}

export function createSaveDecisionActionDTO(
  requestTaskId: number,
  group: UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  reviewSectionsCompleted: Record<string, string>,
  decision: UnderlyingAgreementReviewDecision,
  determination: Determination,
): UnaVariationReviewSaveDecisionDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
      group,
      reviewSectionsCompleted,
      determination,
      decision,
    },
  };
}

export function createSaveFacilityDecisionActionDTO(
  requestTaskId: number,
  facilityId: string,
  reviewSectionsCompleted: Record<string, string>,
  decision: UnderlyingAgreementVariationFacilityReviewDecision,
  determination: Determination,
): UnaVariationReviewSaveFacilityDecisionDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD',
      group: facilityId,
      reviewSectionsCompleted,
      determination,
      decision,
    },
  };
}

export function createSaveDeterminationActionDTO(
  requestTaskId: number,
  determination: Determination,
  reviewSectionsCompleted: Record<string, string>,
): UnaVariationReviewSaveDeterminationDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      determination,
      reviewSectionsCompleted,
    },
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: CcaDecisionNotification,
  proposedPayload: UnderlyingAgreementVariationPayload,
): UnaVariationReviewNotifyDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      underlyingAgreementProposed: proposedPayload,
      decisionNotification,
    },
  };
}

// Payload transformation functions
export function toUnderlyingAgreementVariationReviewSavePayload(
  payload: UnderlyingAgreementVariationReviewRequestTaskPayload,
): UnderlyingAgreementVariationReviewSavePayload {
  if (!payload.underlyingAgreement) throw new Error('Underlying agreement payload is missing');
  return transformUnderlyingAgreement(payload.underlyingAgreement);
}

function transformUnderlyingAgreement(
  underlyingAgreement: UnderlyingAgreementVariationPayload,
): UnderlyingAgreementVariationReviewSavePayload {
  return {
    underlyingAgreementTargetUnitDetails: underlyingAgreement.underlyingAgreementTargetUnitDetails,
    underlyingAgreementVariationDetails: underlyingAgreement.underlyingAgreementVariationDetails,
    facilities: underlyingAgreement.facilities,
    targetPeriod5Details: underlyingAgreement.targetPeriod5Details,
    targetPeriod6Details: underlyingAgreement.targetPeriod6Details,
    authorisationAndAdditionalEvidence: underlyingAgreement.authorisationAndAdditionalEvidence,
  };
}
