import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import { PATUploadedActionPayload } from '../pat-upload-submitted.types';

const selectPayload: StateSelector<RequestActionState, PATUploadedActionPayload> = createDescendingSelector(
  requestActionQuery.selectActionPayload,
  (actionPayload) => actionPayload as PATUploadedActionPayload,
);

export const patUploadSubmittedActionQuery = {
  selectPayload,
};
