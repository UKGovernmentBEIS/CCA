import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  AccountReferenceData,
  Facility,
  SectorAssociationDetails,
  UnderlyingAgreementVariationDetails,
  UnderlyingAgreementVariationPayload,
  UnderlyingAgreementVariationPeerReviewRequestTaskPayload,
} from 'cca-api';

export type UNAVariationPeerReviewTaskPayload = UnderlyingAgreementVariationPeerReviewRequestTaskPayload;

const selectPayload: StateSelector<RequestTaskState, UNAVariationPeerReviewTaskPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (taskPayload) => taskPayload as UNAVariationPeerReviewTaskPayload,
);

const selectUnderlyingAgreement: StateSelector<RequestTaskState, UnderlyingAgreementVariationPayload> =
  createDescendingSelector(selectPayload, (payload) => payload?.underlyingAgreement);

const selectVariationDetails: StateSelector<RequestTaskState, UnderlyingAgreementVariationDetails> =
  createDescendingSelector(
    selectUnderlyingAgreement,
    (underlyingAgreement) => underlyingAgreement?.underlyingAgreementVariationDetails,
  );

const selectUnderlyingAgreementTargetUnitDetails = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement?.underlyingAgreementTargetUnitDetails,
);

const selectTargetPeriod5 = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement?.targetPeriod5Details,
);

const selectTargetPeriod6 = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement?.targetPeriod6Details,
);

const selectFacilities = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement?.facilities || [],
);

const selectManageFacilities = createDescendingSelector(
  selectFacilities,
  (facilities) =>
    facilities?.map((f) => ({
      name: f.facilityDetails.name,
      facilityId: f.facilityId,
      status: f.status,
    })) ?? [],
);

const selectDetermination = createDescendingSelector(selectPayload, (payload) => payload?.determination);

const selectReviewGroupDecisions = createDescendingSelector(
  selectPayload,
  (payload) => payload?.reviewGroupDecisions || {},
);

const selectFacilitiesReviewGroupDecisions = createDescendingSelector(
  selectPayload,
  (payload) => payload?.facilitiesReviewGroupDecisions || {},
);

const selectReviewAttachments = createDescendingSelector(selectPayload, (payload) => payload?.reviewAttachments || {});

const selectUnderlyingAgreementVariationAttachments = createDescendingSelector(
  selectPayload,
  (payload) => payload?.underlyingAgreementAttachments || {},
);

const selectSubtaskDecision = (subtask: string) =>
  createDescendingSelector(selectReviewGroupDecisions, (decisions) => decisions?.[subtask]);

const selectFacilityDecision = (facilityId: string) =>
  createDescendingSelector(selectFacilitiesReviewGroupDecisions, (decisions) => decisions?.[facilityId]);

const selectAccountReferenceData: StateSelector<RequestTaskState, AccountReferenceData> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.accountReferenceData,
);

const selectAccountReferenceDataSectorAssociationDetails: StateSelector<RequestTaskState, SectorAssociationDetails> =
  createDescendingSelector(
    selectAccountReferenceData,
    (accountReferenceData) => accountReferenceData?.sectorAssociationDetails,
  );

const selectSectorAssociationDetailsSchemeData = (scheme: string) =>
  createDescendingSelector(
    selectAccountReferenceDataSectorAssociationDetails,
    (details) => details?.schemeDataMap?.[scheme],
  );

const selectFacility = (facilityId: string): StateSelector<RequestTaskState, Facility> =>
  createDescendingSelector(selectFacilities, (facilities) => facilities?.find((f) => f.facilityId === facilityId));

const selectAuthorisationAndAdditionalEvidence = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement?.authorisationAndAdditionalEvidence,
);

const selectFacilityTargetComposition = (facilityIndex: number) =>
  createDescendingSelector(
    selectUnderlyingAgreement,
    (una) => una?.facilities?.[facilityIndex]?.cca3BaselineAndTargets?.targetComposition,
  );

const selectFacilityBaselineEnergyConsumption = (facilityIndex: number) =>
  createDescendingSelector(
    selectUnderlyingAgreement,
    (una) => una?.facilities?.[facilityIndex]?.cca3BaselineAndTargets?.facilityBaselineEnergyConsumption,
  );

export const underlyingAgreementVariationPeerReviewQuery = {
  selectPayload,
  selectAccountReferenceData,
  selectAccountReferenceDataSectorAssociationDetails,
  selectSectorAssociationDetailsSchemeData,
  selectUnderlyingAgreement,
  selectVariationDetails,
  selectUnderlyingAgreementTargetUnitDetails,
  selectTargetPeriod5,
  selectTargetPeriod6,
  selectManageFacilities,
  selectFacilities,
  selectFacility,
  selectFacilityTargetComposition,
  selectFacilityBaselineEnergyConsumption,
  selectAuthorisationAndAdditionalEvidence,
  selectDetermination,
  selectReviewGroupDecisions,
  selectFacilitiesReviewGroupDecisions,
  selectReviewAttachments,
  selectUnderlyingAgreementVariationAttachments,
  selectSubtaskDecision,
  selectFacilityDecision,
};
