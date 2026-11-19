import { Service } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import {
  AccountPerformanceAccountTemplateDataReportDetailsDTO,
  AccountPerformanceAccountTemplateDataReportInfoDTO,
} from 'cca-api';

export interface PatReportState {
  reportInfo: AccountPerformanceAccountTemplateDataReportInfoDTO;
  reportDetails: AccountPerformanceAccountTemplateDataReportDetailsDTO;
}

const initialState: PatReportState = {
  reportInfo: {
    targetPeriodType: null,
    targetPeriodName: null,
    targetPeriodYear: null,
  },
  reportDetails: {
    targetPeriodName: null,
    targetPeriodType: null,
    targetPeriodYear: null,
    data: {
      targetUnitIdentityAndPerformance: null,
      file: null,
      energyOrCarbonSavingActionsAndMeasuresImplementedItems: null,
    },
  },
};

@Service({ autoProvided: false })
export class PatReportStore extends SignalStore<PatReportState> {
  constructor() {
    super(initialState);
  }
}
