import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { UnderlyingAgreementActivationDetails } from 'cca-api';

import { UNAActivationRequestTaskPayload } from '../underlying-agreement-activation.types';

const selectPayload: StateSelector<RequestTaskState, UNAActivationRequestTaskPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as UNAActivationRequestTaskPayload,
);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.sectionsCompleted,
);

const selectUnderlyingAgreementActivationAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.underlyingAgreementActivationAttachments);

const selectUnderlyingAgreementActivationDetails: StateSelector<
  RequestTaskState,
  UnderlyingAgreementActivationDetails
> = createDescendingSelector(selectPayload, (payload) => payload.underlyingAgreementActivationDetails);

export const underlyingAgreementActivationQuery = {
  selectPayload,
  selectSectionsCompleted,
  selectUnderlyingAgreementActivationAttachments,
  selectUnderlyingAgreementActivationDetails,
};
