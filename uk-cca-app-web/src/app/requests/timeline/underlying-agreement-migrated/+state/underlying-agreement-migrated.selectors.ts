import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import { Facility, FileInfoDTO, UnderlyingAgreementMigratedRequestActionPayload } from 'cca-api';

const selectPayload: StateSelector<RequestActionState, UnderlyingAgreementMigratedRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (actionPayload) => actionPayload as UnderlyingAgreementMigratedRequestActionPayload,
  );

const selectUnderlyingAgreementDocument: StateSelector<RequestActionState, FileInfoDTO> = createDescendingSelector(
  selectPayload,
  (payload) => payload.underlyingAgreementDocument,
);

const selectUnderlyingAgreementFacilities: StateSelector<RequestActionState, Facility[]> = createDescendingSelector(
  selectPayload,
  (payload) => payload.underlyingAgreement.facilities,
);

const selectUnderlyingAgreementAttachments: StateSelector<
  RequestActionState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload.underlyingAgreementAttachments);

export const underlyingAgreementMigratedQuery = {
  selectPayload,
  selectUnderlyingAgreementFacilities,
  selectUnderlyingAgreementDocument,
  selectUnderlyingAgreementAttachments,
};
