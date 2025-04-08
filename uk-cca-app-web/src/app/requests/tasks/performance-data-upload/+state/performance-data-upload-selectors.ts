import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { PerformanceDataUploadPayload } from '../../../common/performance-data/performance-data.types';

const selectPerformanceDataUploadPayload: StateSelector<RequestTaskState, PerformanceDataUploadPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as PerformanceDataUploadPayload,
  );

const selectSectorAssociationInfo = createDescendingSelector(
  selectPerformanceDataUploadPayload,
  (payload) => payload?.sectorAssociationInfo,
);

const selectPerformanceDataUpload = createDescendingSelector(
  selectPerformanceDataUploadPayload,
  (payload) => payload?.performanceDataUpload,
);
const selectProcessCompleted = createDescendingSelector(
  selectPerformanceDataUploadPayload,
  (payload) => payload?.processCompleted,
);

const selectAccountReports = createDescendingSelector(
  selectPerformanceDataUploadPayload,
  (payload) => payload?.accountReports,
);

const selectSuccessfulReportsCount = createDescendingSelector(
  selectPerformanceDataUploadPayload,
  (payload) => payload?.filesSucceeded,
);

const selectFailedReportsCount = createDescendingSelector(
  selectPerformanceDataUploadPayload,
  (payload) => payload?.filesFailed,
);

const selectPerformanceDataUploadAttachments = createDescendingSelector(
  selectPerformanceDataUploadPayload,
  (payload) => payload?.performanceDataUploadAttachments,
);

const selectCsvFile = createDescendingSelector(selectPerformanceDataUploadPayload, (payload) => payload?.csvFile);

const selectErrorMessage = createDescendingSelector(
  selectPerformanceDataUploadPayload,
  (payload) => payload?.errorMessage,
);

const selectErrors = createDescendingSelector(selectPerformanceDataUploadPayload, (payload) => payload?.errors);

export const performanceDataUploadQuery = {
  selectPerformanceDataUploadPayload,
  selectSectorAssociationInfo,
  selectPerformanceDataUpload,
  selectProcessCompleted,
  selectAccountReports,
  selectSuccessfulReportsCount,
  selectFailedReportsCount,
  selectPerformanceDataUploadAttachments,
  selectCsvFile,
  selectErrorMessage,
  selectErrors,
};
