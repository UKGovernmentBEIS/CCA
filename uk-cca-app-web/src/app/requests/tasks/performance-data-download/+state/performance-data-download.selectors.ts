import { createDescendingSelector, RequestTaskState, StateSelector } from '@netz/common/store';
import { requestTaskQuery } from '@netz/common/store';

import { FileInfoDTO, SectorAssociationInfo } from 'cca-api';

import { PerformanceDataDownloadPayload } from '../../../common/performance-data/performance-data.types';

const selectPerformanceDataPayload: StateSelector<RequestTaskState, PerformanceDataDownloadPayload> =
  createDescendingSelector(
    requestTaskQuery.selectRequestTaskPayload,
    (payload) => payload as PerformanceDataDownloadPayload,
  );

const selectSectorAssociationInfo: StateSelector<RequestTaskState, SectorAssociationInfo> = createDescendingSelector(
  selectPerformanceDataPayload,
  (payload: PerformanceDataDownloadPayload) => payload.sectorAssociationInfo,
);

const selectTargetPeriodType: StateSelector<RequestTaskState, string> = createDescendingSelector(
  selectPerformanceDataPayload,
  (payload: PerformanceDataDownloadPayload) => payload?.targetPeriodType,
);

const selectProcessCompleted: StateSelector<RequestTaskState, boolean> = createDescendingSelector(
  selectPerformanceDataPayload,
  (payload: PerformanceDataDownloadPayload) => payload?.processCompleted,
);

const selectZipFile: StateSelector<RequestTaskState, FileInfoDTO> = createDescendingSelector(
  selectPerformanceDataPayload,
  (payload: PerformanceDataDownloadPayload) => payload?.zipFile,
);

const selectErrorsFile: StateSelector<RequestTaskState, FileInfoDTO> = createDescendingSelector(
  selectPerformanceDataPayload,
  (payload: PerformanceDataDownloadPayload) => payload?.errorsFile,
);

const selectErrorMessage: StateSelector<RequestTaskState, string> = createDescendingSelector(
  selectPerformanceDataPayload,
  (payload: PerformanceDataDownloadPayload) => payload?.errorMessage,
);

export const performanceDataDownloadQuery = {
  selectPerformanceDataPayload,
  selectSectorAssociationInfo,
  selectTargetPeriodType,
  selectProcessCompleted,
  selectZipFile,
  selectErrorsFile,
  selectErrorMessage,
};
