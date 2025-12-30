import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  UnderlyingAgreementActivationDetails,
  UnderlyingAgreementVariationActivationRequestTaskPayload,
} from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, UnderlyingAgreementVariationActivationRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as UnderlyingAgreementVariationActivationRequestTaskPayload,
  );

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.sectionsCompleted,
);

const selectAttachments: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.underlyingAgreementActivationAttachments,
);

const selectDetails: StateSelector<RequestTaskState, UnderlyingAgreementActivationDetails> = createDescendingSelector(
  selectPayload,
  (payload) => payload.underlyingAgreementActivationDetails,
);

export const underlyingAgreementVariationActivationQuery = {
  selectPayload,
  selectSectionsCompleted,
  selectAttachments,
  selectDetails,
};
