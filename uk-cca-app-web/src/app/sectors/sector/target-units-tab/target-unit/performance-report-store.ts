import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { AccountPerformanceDataReportDetailsDTO, AccountPerformanceDataStatusInfoDTO } from 'cca-api';

export interface AccountPerformanceDataReportState {
  statusInfo: AccountPerformanceDataStatusInfoDTO;
  reportDetails: AccountPerformanceDataReportDetailsDTO;
}

const initialState: AccountPerformanceDataReportState = {
  statusInfo: {
    locked: null,
    reportVersion: null,
    targetPeriodType: null,
    editable: null,
    targetPeriodName: null,
  },
  reportDetails: {
    targetPeriodReport: null,
    submissionDate: null,
    submissionType: null,
    reportVersion: null,
    energyCarbonUnit: null,
    tpPerformance: null,
    percentTarget: null,
    tpPerformancePercent: null,
    tpOutcome: null,
    carbonSurplusBuyOutDTO: null,
    secondaryMoASurplusBuyOutDTO: null,
  },
};

@Injectable()
export class PerformanceReportStore extends SignalStore<AccountPerformanceDataReportState> {
  constructor() {
    super(initialState);
  }
}
