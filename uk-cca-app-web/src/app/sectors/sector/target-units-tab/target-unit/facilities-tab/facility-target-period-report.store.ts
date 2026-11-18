import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { FacilityPerformanceDataReportDetailsDTO, FacilityPerformanceDataStatusInfoDTO } from 'cca-api';

export interface FacilityTargetPeriodReportsState {
  statusInfo: FacilityPerformanceDataStatusInfoDTO[];
  details: FacilityPerformanceDataReportDetailsDTO;
  reportType: 'INTERIM' | 'FINAL';
  targetPeriodType: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9';
}

const initialState: FacilityTargetPeriodReportsState = {
  statusInfo: [],
  details: null,
  reportType: null,
  targetPeriodType: null,
};

@Injectable()
export class FacilityTargetPeriodReportStore extends SignalStore<FacilityTargetPeriodReportsState> {
  constructor() {
    super(initialState);
  }
}
