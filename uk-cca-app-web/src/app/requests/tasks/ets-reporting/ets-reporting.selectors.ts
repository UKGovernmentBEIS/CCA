import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { EtsReportingTaskPayload } from './ets-reporting.types';

const selectPayload: StateSelector<RequestTaskState, EtsReportingTaskPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as EtsReportingTaskPayload,
);

const selectInputDetails = createDescendingSelector(
  selectPayload,
  (payload) => payload?.emissionsTradingSchemeDataInputDetails,
);

export const etsReportingQuery = {
  selectPayload,
  selectInputDetails,
  selectEnergyFuelDetails: createDescendingSelector(selectInputDetails, (details) => details?.energyFuelDetails),
  selectMeasurementType: createDescendingSelector(selectPayload, (payload) => payload.measurementType),
  selectTargetYear: createDescendingSelector(selectPayload, (payload) => payload.targetYear),
  selectSectionsCompleted: createDescendingSelector(selectPayload, (payload) => payload?.sectionsCompleted ?? {}),
};
