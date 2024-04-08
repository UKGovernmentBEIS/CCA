import { createDescendingSelector, createSelector, StateSelector } from '@common/store';

import { RequestActionDTO, RequestActionPayload } from 'cca-api';

import { RequestActionState } from './request-action.state';

const selectAction: StateSelector<RequestActionState, RequestActionDTO> = createSelector((state) => state.action);

const selectActionType: StateSelector<RequestActionState, RequestActionDTO['type']> = createDescendingSelector(
  selectAction,
  (action) => action?.type,
);

const selectActionPayload: StateSelector<RequestActionState, RequestActionPayload> = createDescendingSelector(
  selectAction,
  (action) => action?.payload,
);

const selectSubmitter: StateSelector<RequestActionState, string> = createDescendingSelector(
  selectAction,
  (action) => action?.submitter,
);

export const requestActionQuery = {
  selectAction,
  selectActionType,
  selectActionPayload,
  selectSubmitter,
};
