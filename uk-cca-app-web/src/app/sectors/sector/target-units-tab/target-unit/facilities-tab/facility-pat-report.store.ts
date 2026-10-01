import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import {
  FacilityPerformanceAccountTemplateDataReportDetailsDTO,
  FacilityPerformanceAccountTemplateDataReportInfoDTO,
} from 'cca-api';

export type ReportingYear = '2026' | '2027' | '2028' | '2029' | '2030';

export interface FacilityPATReportsState {
  reportInfo: FacilityPerformanceAccountTemplateDataReportInfoDTO;
  reportingYear: ReportingYear;
  details?: FacilityPerformanceAccountTemplateDataReportDetailsDTO;
}

const initialState: FacilityPATReportsState = {
  reportInfo: null,
  reportingYear: null,
};

@Injectable()
export class FacilityPATReportStore extends SignalStore<FacilityPATReportsState> {
  constructor() {
    super(initialState);
  }
}
