import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import { PerformanceDataUploadedActionPayload } from '../performance-data-upload-submitted.types';

const selectPayload: StateSelector<RequestActionState, PerformanceDataUploadedActionPayload> = createDescendingSelector(
  requestActionQuery.selectActionPayload,
  (actionPayload) => actionPayload as PerformanceDataUploadedActionPayload,
);

const selectCreationDate: StateSelector<RequestActionState, string> = createDescendingSelector(
  requestActionQuery.selectAction,
  (action) => action?.creationDate,
);

export const performanceDataUploadSubmittedActionQuery = {
  selectPayload,
  selectCreationDate,
};
