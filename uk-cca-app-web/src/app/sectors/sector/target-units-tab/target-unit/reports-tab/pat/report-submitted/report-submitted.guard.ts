import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot } from '@angular/router';

import { PerformanceDataTargetPeriods } from '@requests/common';

import { PatReportStore } from '../../../pat-report-store';

export const reportSubmittedGuardPAT = (route: ActivatedRouteSnapshot) => {
  const patReportStore = inject(PatReportStore);

  if (
    !PerformanceDataTargetPeriods.includes(route.paramMap.get('targetPeriodType')) &&
    route.paramMap.get('reportType') !== 'PAT'
  )
    return createUrlTreeFromSnapshot(route, ['../../../'], { section: 'pat' }, 'reports');

  if (patReportStore.state?.reportDetails?.targetPeriodName == null)
    return createUrlTreeFromSnapshot(route, ['../../../'], { section: 'pat' }, 'reports');

  return true;
};
