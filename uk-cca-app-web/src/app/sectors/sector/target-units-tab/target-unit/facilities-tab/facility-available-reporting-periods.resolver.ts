import { inject } from '@angular/core';

import { SchemeVersionEnum } from '@shared/types';

import { PerformanceDataReportingViewInfoService } from 'cca-api';

export const FacilityAvailableReportingPeriodsResolver = () => {
  return inject(PerformanceDataReportingViewInfoService).getAvailableTargetPeriodsForPerformanceDataReporting(
    SchemeVersionEnum.CCA_3,
  );
};
