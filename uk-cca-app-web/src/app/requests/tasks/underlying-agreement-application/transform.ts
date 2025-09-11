import {
  Facility,
  FacilityItem,
  RequestTaskActionProcessDTO,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementPayload,
  UnderlyingAgreementSaveRequestTaskActionPayload,
  UnderlyingAgreementSubmitRequestTaskPayload,
} from 'cca-api';

type UnaRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementSaveRequestTaskActionPayload;
};

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: UnderlyingAgreementApplySavePayload,
  sectionsCompleted: Record<string, string>,
): UnaRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_APPLICATION_SAVE_PAYLOAD',
      underlyingAgreement: payload,
      sectionsCompleted,
    },
  };
}

export function toUnderlyingAgreementSavePayload(
  payload: UnderlyingAgreementSubmitRequestTaskPayload,
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
