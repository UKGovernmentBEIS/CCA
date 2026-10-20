import {
  PerformanceData,
  PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload,
  TP6PerformanceDataAllOf,
} from 'cca-api';

export type PerformanceDataTp6 = PerformanceData & TP6PerformanceDataAllOf;

export type PerformanceDataUploadedActionPayload = Omit<
  PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload,
  'performanceData'
> & {
  performanceData?: PerformanceDataTp6;
};
