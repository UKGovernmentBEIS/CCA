import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import { EmissionsTradingSchemeDataSubmittedRequestActionPayload } from 'cca-api';

const selectPayload: StateSelector<RequestActionState, EmissionsTradingSchemeDataSubmittedRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (payload) => payload as EmissionsTradingSchemeDataSubmittedRequestActionPayload,
  );

const selectPerformanceData = createDescendingSelector(selectPayload, (payload) => payload?.performanceData);
const selectCalculatedResults = createDescendingSelector(selectPayload, (payload) => payload?.calculatedResults);

export const etsReportingActionQuery = {
  selectPayload,
  selectPerformanceData,
  selectCalculatedResults,
};
