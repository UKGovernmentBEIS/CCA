import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  AccountReferenceData,
  Facility,
  SectorAssociationDetails,
  UnderlyingAgreementPeerReviewRequestTaskPayload,
} from 'cca-api';

export type UNAPeerReviewTaskPayload = UnderlyingAgreementPeerReviewRequestTaskPayload;

const selectPayload: StateSelector<RequestTaskState, UNAPeerReviewTaskPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (taskPayload) => taskPayload as UNAPeerReviewTaskPayload,
);

const selectUnderlyingAgreement = createDescendingSelector(selectPayload, (payload) => payload?.underlyingAgreement);

const selectUnderlyingAgreementTargetUnitDetails = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement?.underlyingAgreementTargetUnitDetails,
);

const selectManageFacilities = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) =>
    underlyingAgreement?.facilities?.map((f) => ({
      name: f.facilityDetails.name,
      facilityId: f.facilityId,
      status: f.status,
    })) ?? [],
);

const selectFacilities = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement?.facilities || [],
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

const selectUnderlyingAgreementAttachments = createDescendingSelector(
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

export const underlyingAgreementPeerReviewQuery = {
  selectPayload,
  selectAccountReferenceData,
  selectAccountReferenceDataSectorAssociationDetails,
  selectSectorAssociationDetailsSchemeData,
  selectUnderlyingAgreement,
  selectUnderlyingAgreementTargetUnitDetails,
  selectManageFacilities,
  selectFacilities,
  selectFacility,
  selectFacilityTargetComposition,
  selectFacilityBaselineEnergyConsumption,
  selectDetermination,
  selectReviewGroupDecisions,
  selectFacilitiesReviewGroupDecisions,
  selectReviewAttachments,
  selectUnderlyingAgreementAttachments,
  selectSubtaskDecision,
  selectFacilityDecision,
};
