import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import { FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload } from 'cca-api';

const selectPayload: StateSelector<
  RequestTaskState,
  FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload
> = createDescendingSelector(requestTaskQuery.selectRequestTaskPayload, (payload) => payload);

const selectSectorAssociationInfo = createDescendingSelector(
  selectPayload,
  (payload) => payload?.sectorAssociationInfo,
);

const selectPerformanceAccountTemplateDataUpload = createDescendingSelector(
  selectPayload,
  (payload) => payload?.performanceAccountTemplateDataUpload,
);

const selectProcessingStatus = createDescendingSelector(selectPayload, (payload) => payload?.processingStatus);
const selectResults = createDescendingSelector(selectPayload, (payload) => payload?.results);
const selectErrorMessage = createDescendingSelector(selectPayload, (payload) => payload?.errorMessage);
const selectUploadAttachments = createDescendingSelector(selectPayload, (payload) => payload?.uploadAttachments);

export const patCsvUploadQuery = {
  selectPayload,
  selectSectorAssociationInfo,
  selectPerformanceAccountTemplateDataUpload,
  selectProcessingStatus,
  selectResults,
  selectErrorMessage,
  selectUploadAttachments,
};
