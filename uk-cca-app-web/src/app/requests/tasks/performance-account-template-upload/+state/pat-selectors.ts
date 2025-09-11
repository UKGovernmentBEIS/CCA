import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { PATUploadPayload } from '../pat.types';

const selectPATUploadPayload: StateSelector<RequestTaskState, PATUploadPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as PATUploadPayload,
);

const selectFileReports = createDescendingSelector(selectPATUploadPayload, (payload) => payload?.fileReports);

const selectCsvReportFile = createDescendingSelector(selectPATUploadPayload, (payload) => payload?.csvReportFile);

const selectPATUploadAttachments = createDescendingSelector(
  selectPATUploadPayload,
  (payload) => payload?.uploadAttachments,
);

const selectProcessingStatus = createDescendingSelector(selectPATUploadPayload, (payload) => payload?.processingStatus);

const selectErrorType = createDescendingSelector(selectPATUploadPayload, (payload) => payload?.errorType);

export const PATUploadQuery = {
  selectPATUploadPayload,
  selectFileReports,
  selectCsvReportFile,
  selectPATUploadAttachments,
  selectProcessingStatus,
  selectErrorType,
};
