import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { PerformanceDataFacilityDataUploadSubmitRequestTaskPayload } from 'cca-api';

const selectPayload: StateSelector<RequestTaskState, PerformanceDataFacilityDataUploadSubmitRequestTaskPayload> =
  createDescendingSelector(requestTaskQuery.selectRequestTaskPayload, (payload) => payload);

const selectSectorAssociationInfo = createDescendingSelector(
  selectPayload,
  (payload) => payload?.sectorAssociationInfo,
);

const selectPerformanceDataUpload = createDescendingSelector(
  selectPayload,
  (payload) => payload?.performanceDataUpload,
);

const selectProcessingStatus = createDescendingSelector(selectPayload, (payload) => payload?.processingStatus);
const selectResults = createDescendingSelector(selectPayload, (payload) => payload?.results);
const selectErrorMessage = createDescendingSelector(selectPayload, (payload) => payload?.errorMessage);
const selectFacilityReports = createDescendingSelector(selectPayload, (payload) => payload?.facilityReports);
const selectCSVRowErrors = createDescendingSelector(selectPayload, (payload) => payload?.csvRowErrors);
const selectUploadAttachments = createDescendingSelector(selectPayload, (payload) => payload?.uploadAttachments);

export const tprCSVUploadQuery = {
  selectPayload,
  selectSectorAssociationInfo,
  selectPerformanceDataUpload,
  selectProcessingStatus,
  selectResults,
  selectErrorMessage,
  selectFacilityReports,
  selectCSVRowErrors,
  selectUploadAttachments,
};
