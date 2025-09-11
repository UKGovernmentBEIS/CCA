import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import {
  AccountReferenceData,
  AuthorisationAndAdditionalEvidence,
  Facility,
  SectorAssociationDetails,
  TargetPeriod5Details,
  TargetPeriod6Details,
  UnderlyingAgreementTargetUnitDetails,
  UnderlyingAgreementVariationDetails,
  UnderlyingAgreementVariationPayload,
} from 'cca-api';

import { FacilityItemViewModel } from '../underlying-agreement';
import { UnaPayload, UnderlyingAgreementRequestActionPayload } from './timeline-underlying-agreement.types';

const selectPayload: StateSelector<RequestActionState, UnderlyingAgreementRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (actionPayload) => actionPayload as UnderlyingAgreementRequestActionPayload,
  );

const selectAccountReferenceData: StateSelector<RequestActionState, AccountReferenceData> = createDescendingSelector(
  selectPayload,
  (payload) => payload.accountReferenceData,
);

const selectAccountReferenceDataSectorAssociationDetails: StateSelector<RequestActionState, SectorAssociationDetails> =
  createDescendingSelector(
    selectAccountReferenceData,
    (accountReferenceData) => accountReferenceData.sectorAssociationDetails,
  );

const selectSectorAssociationDetailsSchemeData = (scheme: string) =>
  createDescendingSelector(
    selectAccountReferenceDataSectorAssociationDetails,
    (details) => details?.schemeDataMap[scheme],
  );

const selectAttachments: StateSelector<RequestActionState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.underlyingAgreementAttachments,
);

const selectUnderlyingAgreement: StateSelector<RequestActionState, UnaPayload> = createDescendingSelector(
  selectPayload,
  (payload) => payload.underlyingAgreement,
);

const selectUnderlyingAgreementTargetUnitDetails: StateSelector<
  RequestActionState,
  UnderlyingAgreementTargetUnitDetails
> = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement.underlyingAgreementTargetUnitDetails,
);

const selectFacilityItems: StateSelector<RequestActionState, FacilityItemViewModel[]> = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) =>
    underlyingAgreement?.facilities?.map((f) => ({
      name: f.facilityDetails.name,
      facilityId: f.facilityId,
      status: f.status,
    })) ?? [],
);

const selectFacility = (facilityId: string): StateSelector<RequestActionState, Facility> => {
  return createDescendingSelector(selectUnderlyingAgreement, (underlyingAgreement) =>
    underlyingAgreement?.facilities ? underlyingAgreement.facilities.find((f) => f.facilityId === facilityId) : null,
  );
};

const selectTargetPeriod5Details: StateSelector<RequestActionState, TargetPeriod5Details> = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement.targetPeriod5Details,
);

const selectTargetPeriod6Details: StateSelector<RequestActionState, TargetPeriod6Details> = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement.targetPeriod6Details,
);

const selectAuthorisationAndAdditionalEvidence: StateSelector<RequestActionState, AuthorisationAndAdditionalEvidence> =
  createDescendingSelector(
    selectUnderlyingAgreement,
    (underlyingAgreement) => underlyingAgreement.authorisationAndAdditionalEvidence,
  );

const selectVariationDetails: StateSelector<RequestActionState, UnderlyingAgreementVariationDetails> =
  createDescendingSelector(
    selectUnderlyingAgreement,
    (underlyingAgreement) =>
      (underlyingAgreement as UnderlyingAgreementVariationPayload).underlyingAgreementVariationDetails,
  );

export const underlyingAgreementRequestActionQuery = {
  selectPayload,
  selectAccountReferenceData,
  selectAccountReferenceDataSectorAssociationDetails,
  selectSectorAssociationDetailsSchemeData,
  selectAttachments,
  selectUnderlyingAgreement,
  selectUnderlyingAgreementTargetUnitDetails,
  selectManageFacilities: selectFacilityItems,
  selectFacility,
  selectTargetPeriod5Details,
  selectTargetPeriod6Details,
  selectAuthorisationAndAdditionalEvidence,
  selectVariationDetails,
};
