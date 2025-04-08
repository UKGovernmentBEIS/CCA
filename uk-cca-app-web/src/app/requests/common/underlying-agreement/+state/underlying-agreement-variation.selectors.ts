import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  AuthorisationAndAdditionalEvidence,
  Facility,
  UnderlyingAgreementContainer,
  UnderlyingAgreementVariationDetails,
  UnderlyingAgreementVariationPayload,
} from 'cca-api';

import { UNAVariationRequestTaskPayload } from '../underlying-agreement.types';

const selectPayload: StateSelector<RequestTaskState, UNAVariationRequestTaskPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as UNAVariationRequestTaskPayload,
);

const selectUnderlyingAgreement: StateSelector<RequestTaskState, UnderlyingAgreementVariationPayload> =
  createDescendingSelector(selectPayload, (payload) => payload.underlyingAgreement);

const selectVariationDetails: StateSelector<RequestTaskState, UnderlyingAgreementVariationDetails> =
  createDescendingSelector(
    selectUnderlyingAgreement,
    (una) => (una as UnderlyingAgreementVariationPayload).underlyingAgreementVariationDetails,
  );

const selectOriginalUnderlyingAgreementContainer: StateSelector<RequestTaskState, UnderlyingAgreementContainer> =
  createDescendingSelector(selectPayload, (payload) => payload.originalUnderlyingAgreementContainer);

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

export const underlyingAgreementVariationQuery = {
  selectVariationDetails,
  selectOriginalUnderlyingAgreementContainer,
  selectOriginalFacility,
  selectOriginalUnderlyingAgreementAttachments,
  selectOriginalAuthorisationAndAdditionalEvidence,
  selectOriginalBaselineExists,
  selectOriginalTargetPeriodDetails,
};
