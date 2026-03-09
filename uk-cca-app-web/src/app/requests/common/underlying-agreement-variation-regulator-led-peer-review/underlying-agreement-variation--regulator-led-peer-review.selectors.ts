import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  AccountReferenceData,
  CcaPeerReviewDecision,
  Facility,
  FacilityBaselineEnergyConsumption,
  FacilityTargetComposition,
  SchemeData,
  SectorAssociationDetails,
  TargetPeriod5Details,
  TargetPeriod6Details,
  UnderlyingAgreementVariationPayload,
  UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload,
} from 'cca-api';

import { FacilityTimelineItemViewModel } from '../underlying-agreement/types';

type UNARegulatorLedVariationPeerReviewTaskPayload =
  UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload;

const selectPayload: StateSelector<RequestTaskState, UNARegulatorLedVariationPeerReviewTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (taskPayload) => taskPayload as UNARegulatorLedVariationPeerReviewTaskPayload,
  );

const selectUnderlyingAgreement: StateSelector<RequestTaskState, UnderlyingAgreementVariationPayload> =
  createDescendingSelector(selectPayload, (payload) => payload?.underlyingAgreement);

const selectTargetPeriod5: StateSelector<RequestTaskState, TargetPeriod5Details> = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement?.targetPeriod5Details,
);

const selectTargetPeriod6: StateSelector<RequestTaskState, TargetPeriod6Details> = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement?.targetPeriod6Details,
);

const selectFacilities: StateSelector<RequestTaskState, Facility[]> = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => underlyingAgreement?.facilities || [],
);

const selectManageFacilities: StateSelector<RequestTaskState, FacilityTimelineItemViewModel[]> =
  createDescendingSelector(
    selectPayload,
    (payload) =>
      payload?.underlyingAgreement?.facilities?.map((f) => ({
        name: f.facilityDetails.name,
        facilityId: f.facilityId,
        status: f.status,
        chargeStartDate: payload.facilityChargeStartDateMap[f.facilityId],
      })) ?? [],
  );

const selectDecision: StateSelector<RequestTaskState, CcaPeerReviewDecision> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.decision,
);

const selectPeerReviewAttachments: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.peerReviewAttachments || {},
);

const selectAccountReferenceData: StateSelector<RequestTaskState, AccountReferenceData> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.accountReferenceData,
);

const selectAccountReferenceDataSectorAssociationDetails: StateSelector<RequestTaskState, SectorAssociationDetails> =
  createDescendingSelector(
    selectAccountReferenceData,
    (accountReferenceData) => accountReferenceData?.sectorAssociationDetails,
  );

const selectSectorAssociationDetailsSchemeData = (scheme: string): StateSelector<RequestTaskState, SchemeData> =>
  createDescendingSelector(
    selectAccountReferenceDataSectorAssociationDetails,
    (details) => details?.schemeDataMap?.[scheme],
  );

const selectFacility = (facilityId: string): StateSelector<RequestTaskState, Facility> =>
  createDescendingSelector(selectFacilities, (facilities) => facilities?.find((f) => f.facilityId === facilityId));

const selectFacilityTargetComposition = (
  facilityIndex: number,
): StateSelector<RequestTaskState, FacilityTargetComposition> =>
  createDescendingSelector(
    selectUnderlyingAgreement,
    (una) => una?.facilities?.[facilityIndex]?.cca3BaselineAndTargets?.targetComposition,
  );

const selectFacilityBaselineEnergyConsumption = (
  facilityIndex: number,
): StateSelector<RequestTaskState, FacilityBaselineEnergyConsumption> =>
  createDescendingSelector(
    selectUnderlyingAgreement,
    (una) => una?.facilities?.[facilityIndex]?.cca3BaselineAndTargets?.facilityBaselineEnergyConsumption,
  );

export const unaRegulatorLedVariationPeerReviewQuery = {
  selectPayload,
  selectAccountReferenceData,
  selectAccountReferenceDataSectorAssociationDetails,
  selectSectorAssociationDetailsSchemeData,
  selectUnderlyingAgreement,
  selectTargetPeriod5,
  selectTargetPeriod6,
  selectManageFacilities,
  selectFacilities,
  selectFacility,
  selectFacilityTargetComposition,
  selectFacilityBaselineEnergyConsumption,
  selectDecision,
  selectPeerReviewAttachments,
};
