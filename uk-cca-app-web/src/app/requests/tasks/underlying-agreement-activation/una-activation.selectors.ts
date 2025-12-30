import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { UnderlyingAgreementActivationDetails, UnderlyingAgreementActivationRequestTaskPayload } from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, UnderlyingAgreementActivationRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as UnderlyingAgreementActivationRequestTaskPayload,
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

export const underlyingAgreementActivationQuery = {
  selectPayload,
  selectSectionsCompleted,
  selectAttachments,
  selectDetails,
};
