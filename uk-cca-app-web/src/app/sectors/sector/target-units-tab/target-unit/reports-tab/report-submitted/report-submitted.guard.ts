import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot } from '@angular/router';

import { PerformanceDataTargetPeriods } from '@requests/common';

import { PerformanceReportStore } from '../../performance-report-store';

export const reportSubmittedGuard = (route: ActivatedRouteSnapshot) => {
  const performanceReportStore = inject(PerformanceReportStore);

  if (!PerformanceDataTargetPeriods.includes(route.paramMap.get('targetPeriodType')))
    return createUrlTreeFromSnapshot(route, ['../../'], null, 'reports');

  // handles page reload when on details page or forced navigation when the reportVersion is 0
  if (
    performanceReportStore.state?.reportDetails?.reportVersion == null ||
    performanceReportStore.state?.reportDetails?.reportVersion === 0
  )
    return createUrlTreeFromSnapshot(route, ['../../'], null, 'reports');

  return true;
};
