import { inject } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { Observable, tap } from 'rxjs';

import {
  FacilityPerformanceDataReportDetailsDTO,
  TargetPeriodPerformanceDataReportOfTheFacilityService,
} from 'cca-api';

import { FacilityTargetPeriodReportStore } from '../../../facility-target-period-report.store';

export const TPRDetailsResolver = (
  route: ActivatedRouteSnapshot,
): Observable<FacilityPerformanceDataReportDetailsDTO> => {
  const facilityId = +route.paramMap.get('facilityId');
  const targetPeriodYear = route.paramMap.get('targetPeriodYear');

  const store = inject(FacilityTargetPeriodReportStore);

  const targetPeriodPerformanceDataReportOfTheFacilityService = inject(
    TargetPeriodPerformanceDataReportOfTheFacilityService,
  );

  return targetPeriodPerformanceDataReportOfTheFacilityService
    .getFacilityPerformanceDataReportDetails(facilityId, targetPeriodYear)
    .pipe(tap((details) => store.updateState({ details })));
};
