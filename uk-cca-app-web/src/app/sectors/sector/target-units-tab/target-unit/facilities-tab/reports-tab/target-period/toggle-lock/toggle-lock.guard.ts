import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot } from '@angular/router';

import { FacilityTargetPeriodReportStore } from '../../../facility-target-period-report.store';

export const toggleLockGuard = (route: ActivatedRouteSnapshot) => {
  const facilityTargetPeriodReportStore = inject(FacilityTargetPeriodReportStore);

  const targetPeriodYear = route.paramMap.get('targetPeriodYear');

  const statusInfo = facilityTargetPeriodReportStore.state.statusInfo.find(
    (i) => Number(i.targetPeriodYear) === Number(targetPeriodYear),
  );

  if (!statusInfo?.lockEditable) return createUrlTreeFromSnapshot(route, ['../../../'], null, 'reports');

  return true;
};
