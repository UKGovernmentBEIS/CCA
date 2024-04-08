import { MiReportResult } from 'cca-api';

export interface ExtendedMiReportResult extends MiReportResult {
  results: Array<any>;
}
