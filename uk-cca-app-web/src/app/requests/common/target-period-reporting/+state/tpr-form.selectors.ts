import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  FacilityBaseInfoDTO,
  PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
  PerformanceDataFacilityInputData,
  PerformanceDataFacilityReferenceData,
} from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
  );

const selectFacility: StateSelector<RequestTaskState, FacilityBaseInfoDTO> = createDescendingSelector(
  selectPayload,
  (payload) => payload.facility,
);

const selectTargetPeriodType: StateSelector<RequestTaskState, 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9'> =
  createDescendingSelector(selectPayload, (payload) => payload.targetPeriodType);

const selectReportType: StateSelector<RequestTaskState, 'INTERIM' | 'FINAL'> = createDescendingSelector(
  selectPayload,
  (payload) => payload.reportType,
);

const selectTargetPeriodYear: StateSelector<RequestTaskState, number> = createDescendingSelector(
  selectPayload,
  (payload) => payload.targetPeriodYear,
);

const selectReferenceData: StateSelector<RequestTaskState, PerformanceDataFacilityReferenceData> =
  createDescendingSelector(selectPayload, (payload) => payload.referenceData);

const selectPerformanceData: StateSelector<RequestTaskState, PerformanceDataFacilityInputData> =
  createDescendingSelector(selectPayload, (payload) => payload.performanceData);

const selectSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.sectionsCompleted,
);

export const tprFormQuery = {
  selectPayload,
  selectTargetPeriodType,
  selectReportType,
  selectTargetPeriodYear,
  selectFacility,
  selectReferenceData,
  selectPerformanceData,
  selectSectionsCompleted,
};
