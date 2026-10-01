import { PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload, TP6PerformanceData } from 'cca-api';

export type PerformanceDataTp6 = TP6PerformanceData;

export type PerformanceDataUploadedActionPayload = Omit<
  PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload,
  'performanceData'
> & {
  performanceData?: PerformanceDataTp6;
};
