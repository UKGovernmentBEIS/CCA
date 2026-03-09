import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  AuthorisationAndAdditionalEvidence,
  Facility,
  UnderlyingAgreementContainer,
  UnderlyingAgreementVariationDetails,
  UnderlyingAgreementVariationPayload,
  VariationRegulatorLedDetermination,
} from 'cca-api';

import { UNAVariationRegulatorLedRequestTaskPayload } from '../types';

const selectPayload: StateSelector<RequestTaskState, UNAVariationRegulatorLedRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as UNAVariationRegulatorLedRequestTaskPayload,
  );

const selectUnderlyingAgreement: StateSelector<RequestTaskState, UnderlyingAgreementVariationPayload> =
  createDescendingSelector(selectPayload, (payload) => payload.underlyingAgreement);

const selectUnderlyingAgreementAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload.underlyingAgreementAttachments);

const selectRegulatorLedSubmitAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload.regulatorLedSubmitAttachments);

const selectOriginalUnderlyingAgreementContainer: StateSelector<RequestTaskState, UnderlyingAgreementContainer> =
  createDescendingSelector(selectPayload, (payload) => payload.originalUnderlyingAgreementContainer);

const selectFacilityChargeStartDateMap: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload.facilityChargeStartDateMap);

const selectFacilityChargeStartDate = (facilityId: string): StateSelector<RequestTaskState, string> =>
  createDescendingSelector(selectPayload, (payload) => payload.facilityChargeStartDateMap[facilityId]);

const selectDetermination: StateSelector<RequestTaskState, VariationRegulatorLedDetermination> =
  createDescendingSelector(selectPayload, (payload) => payload.determination);

const selectVariationDetails: StateSelector<RequestTaskState, UnderlyingAgreementVariationDetails> =
  createDescendingSelector(selectUnderlyingAgreement, (una) => una.underlyingAgreementVariationDetails);

const selectAuthorisationAndAdditionalEvidence: StateSelector<RequestTaskState, AuthorisationAndAdditionalEvidence> =
  createDescendingSelector(selectUnderlyingAgreement, (una) => una.authorisationAndAdditionalEvidence);

const selectOriginalFacility = (facilityId: string): StateSelector<RequestTaskState, Facility> => {
  return createDescendingSelector(selectOriginalUnderlyingAgreementContainer, (originalDataContainer) =>
    originalDataContainer?.underlyingAgreement.facilities
      ? originalDataContainer?.underlyingAgreement.facilities.find((f) => f.facilityId === facilityId)
      : null,
  );
};

const selectOriginalUnderlyingAgreementAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(
  selectOriginalUnderlyingAgreementContainer,
  (originalDataContainer) => originalDataContainer?.underlyingAgreementAttachments,
);

const selectOriginalAuthorisationAndAdditionalEvidence: StateSelector<
  RequestTaskState,
  AuthorisationAndAdditionalEvidence
> = createDescendingSelector(
  selectOriginalUnderlyingAgreementContainer,
  (originalDataContainer) => originalDataContainer?.underlyingAgreement.authorisationAndAdditionalEvidence,
);

const selectOriginalBaselineExists: StateSelector<RequestTaskState, boolean> = createDescendingSelector(
  selectOriginalUnderlyingAgreementContainer,
  (originalDataContainer) => originalDataContainer?.underlyingAgreement.targetPeriod5Details.exist,
);

const selectOriginalTargetPeriodDetails = (isTP5: boolean) =>
  createDescendingSelector(selectOriginalUnderlyingAgreementContainer, (originalDataContainer) =>
    isTP5
      ? originalDataContainer?.underlyingAgreement.targetPeriod5Details.details
      : originalDataContainer?.underlyingAgreement.targetPeriod6Details,
  );

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.sectionsCompleted,
);

export const underlyingAgreementVariationRegulatorLedQuery = {
  selectVariationDetails,
  selectUnderlyingAgreementAttachments,
  selectRegulatorLedSubmitAttachments,
  selectAuthorisationAndAdditionalEvidence,
  selectFacilityChargeStartDateMap,
  selectFacilityChargeStartDate,
  selectDetermination,
  selectOriginalUnderlyingAgreementContainer,
  selectOriginalFacility,
  selectOriginalUnderlyingAgreementAttachments,
  selectOriginalAuthorisationAndAdditionalEvidence,
  selectOriginalBaselineExists,
  selectOriginalTargetPeriodDetails,
  selectSectionsCompleted,
};
