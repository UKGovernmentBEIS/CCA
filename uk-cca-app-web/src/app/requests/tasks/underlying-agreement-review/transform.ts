import {
  CcaDecisionNotification,
  Determination,
  Facility,
  FacilityItem,
  RequestTaskActionProcessDTO,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementFacilityReviewDecision,
  UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload,
  UnderlyingAgreementPayload,
  UnderlyingAgreementReviewDecision,
  UnderlyingAgreementReviewRequestTaskPayload,
  UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload,
  UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementSaveReviewRequestTaskActionPayload,
} from 'cca-api';

type UnaReviewSaveActionDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementSaveReviewRequestTaskActionPayload;
};

type UnaReviewSaveDecisionDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload;
};

type UnaReviewSaveFacilityDecisionDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
};

type UnaReviewSaveDeterminationDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload;
};

type UnaReviewNotifyDto = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload;
};

export function createSaveActionDTO(
  requestTaskId: number,
  underlyingAgreement: UnderlyingAgreementApplySavePayload,
  requestTaskProps: {
    sectionsCompleted: Record<string, string>;
    reviewSectionsCompleted: Record<string, string>;
    determination: Determination;
  },
): UnaReviewSaveActionDTO {
  const { determination, reviewSectionsCompleted, sectionsCompleted } = requestTaskProps;

  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_SAVE_APPLICATION_REVIEW',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_APPLICATION_REVIEW_PAYLOAD',
      underlyingAgreement,
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
    },
  };
}
export function createSaveDecisionActionDTO(
  requestTaskId: number,
  group: UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  reviewSectionsCompleted: Record<string, string>,
  decision: UnderlyingAgreementReviewDecision,
  determination: Determination,
): UnaReviewSaveDecisionDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
      group,
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
): UnaReviewSaveDeterminationDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      determination,
      reviewSectionsCompleted,
    },
  };
}

export function createSaveFacilityDecisionActionDTO(
  requestTaskId: number,
  facilityId: string,
  reviewSectionsCompleted: Record<string, string>,
  decision: UnderlyingAgreementFacilityReviewDecision,
  determination: Determination,
): UnaReviewSaveFacilityDecisionDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD',
      group: facilityId,
      reviewSectionsCompleted,
      determination,
      decision,
    },
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: CcaDecisionNotification,
  proposedPayload: UnderlyingAgreementPayload,
): UnaReviewNotifyDto {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_NOTIFY_OPERATOR_FOR_DECISION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      underlyingAgreementProposed: proposedPayload,
      decisionNotification,
    },
  };
}

export function toUnderlyingAgreementSaveReviewPayload(
  payload: UnderlyingAgreementReviewRequestTaskPayload,
): UnderlyingAgreementApplySavePayload {
  if (!payload.underlyingAgreement) throw new Error('Underlying agreement payload is missing');
  return transformUnderlyingAgreement(payload.underlyingAgreement);
}

function transformUnderlyingAgreement(
  underlyingAgreement: UnderlyingAgreementPayload,
): UnderlyingAgreementApplySavePayload {
  return {
    underlyingAgreementTargetUnitDetails: underlyingAgreement.underlyingAgreementTargetUnitDetails,
    facilities: transformFacilities(underlyingAgreement.facilities),
    authorisationAndAdditionalEvidence: underlyingAgreement.authorisationAndAdditionalEvidence,
  };
}

function transformFacilities(facilities: Facility[]): FacilityItem[] {
  return (
    facilities?.map((facility) => ({
      facilityId: facility.facilityId,
      facilityDetails: facility.facilityDetails,
      apply70Rule: facility.apply70Rule,
      eligibilityDetailsAndAuthorisation: facility.eligibilityDetailsAndAuthorisation,
      facilityContact: facility.facilityContact,
      facilityExtent: facility.facilityExtent,
      cca3BaselineAndTargets: facility.cca3BaselineAndTargets,
    })) || []
  );
}
