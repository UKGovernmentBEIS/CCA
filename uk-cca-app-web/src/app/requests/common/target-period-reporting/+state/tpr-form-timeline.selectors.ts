import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import {
  PerformanceDataFacilityContainer,
  PerformanceDataFacilitySubmissionDetails,
  PerformanceDataFacilitySubmittedRequestActionPayload,
} from 'cca-api';

const selectPayload: StateSelector<RequestActionState, PerformanceDataFacilitySubmittedRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (actionPayload) => actionPayload as PerformanceDataFacilitySubmittedRequestActionPayload,
  );

const selectDetails: StateSelector<RequestActionState, PerformanceDataFacilitySubmissionDetails> =
  createDescendingSelector(selectPayload, (actionPayload) => actionPayload?.details);

const selectPerformanceData: StateSelector<RequestActionState, PerformanceDataFacilityContainer> =
  createDescendingSelector(selectPayload, (actionPayload) => actionPayload?.performanceData);

export const tprFormActionQuery = {
  selectPayload,
  selectDetails,
  selectPerformanceData,
};
