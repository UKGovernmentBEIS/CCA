import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { UnderlyingAgreementActivationDetails } from 'cca-api';

import { CCA3MigrationRequestTaskPayload } from '../types';

const selectPayload: StateSelector<RequestTaskState, CCA3MigrationRequestTaskPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as CCA3MigrationRequestTaskPayload,
);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.sectionsCompleted,
);

const selectCca3MigrationAccountActivationAttachments: StateSelector<
  RequestTaskState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload?.activationAttachments);

const selectCca3MigrationAccountActivationDetails: StateSelector<
  RequestTaskState,
  UnderlyingAgreementActivationDetails
> = createDescendingSelector(selectPayload, (payload) => payload.activationDetails);

export const cca3MigrationAccountActivationQuery = {
  selectPayload,
  selectSectionsCompleted,
  selectCca3MigrationAccountActivationAttachments,
  selectCca3MigrationAccountActivationDetails,
};
