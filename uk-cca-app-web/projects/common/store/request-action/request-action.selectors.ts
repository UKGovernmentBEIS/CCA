import { RequestActionDTO, RequestActionPayload } from 'cca-api';

import { createDescendingSelector, createSelector, StateSelector } from '../index';
import { RequestActionState } from './request-action.state';

const selectAction: StateSelector<RequestActionState, RequestActionDTO | null> = createSelector(
  (state) => state.action,
);

const selectActionType: StateSelector<RequestActionState, RequestActionDTO['type'] | undefined> =
  createDescendingSelector(selectAction, (action) => action?.type);

const selectActionPayload: StateSelector<RequestActionState, RequestActionPayload | undefined> =
  createDescendingSelector(selectAction, (action) => action?.payload);

const selectSubmitter: StateSelector<RequestActionState, string | undefined> = createDescendingSelector(
  selectAction,
  (action) => action?.submitter,
);

export const requestActionQuery = {
  selectAction,
  selectActionType,
  selectActionPayload,
  selectSubmitter,
};
