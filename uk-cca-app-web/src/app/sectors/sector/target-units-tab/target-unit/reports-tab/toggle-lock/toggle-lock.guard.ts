import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot } from '@angular/router';

import { PerformanceDataTargetPeriods } from '@requests/common';

import { PerformanceReportStore } from '../../performance-report-store';

export const toggleLockGuard = (route: ActivatedRouteSnapshot) => {
  const performanceReportStore = inject(PerformanceReportStore);

  if (!isValidPerformanceDataTargetPeriod(route.paramMap.get('targetPeriodType')))
    return createUrlTreeFromSnapshot(route, ['../../'], null, 'reports');

  if (!performanceReportStore.stateAsSignal().statusInfo.editable)
    return createUrlTreeFromSnapshot(route, ['../../'], null, 'reports');

  return true;
};

function isValidPerformanceDataTargetPeriod(input: string): boolean {
  return PerformanceDataTargetPeriods.includes(input);
}
