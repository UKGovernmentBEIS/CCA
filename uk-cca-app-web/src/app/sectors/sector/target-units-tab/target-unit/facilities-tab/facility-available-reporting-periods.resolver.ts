import { inject } from '@angular/core';

import { PerformanceDataReportingViewInfoService } from 'cca-api';

export const FacilityAvailableReportingPeriodsResolver = () => {
  return inject(PerformanceDataReportingViewInfoService).getAvailableTargetPeriodsForPerformanceDataReporting('CCA_3');
};
