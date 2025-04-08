import {
  PerformanceDataDownloadSubmitRequestTaskPayload,
  PerformanceDataUploadSubmitRequestTaskPayload,
} from 'cca-api';

export type PerformanceDataDownloadPayload = PerformanceDataDownloadSubmitRequestTaskPayload;
export type PerformanceDataUploadPayload = PerformanceDataUploadSubmitRequestTaskPayload;

export enum PerformanceDataTargetPeriodEnum {
  TP6 = 'TP6',
}

export const PerformanceDataTargetPeriods = ['TP6'];
