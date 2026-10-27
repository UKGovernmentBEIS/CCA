import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  TaskItemStatus,
  TPR_FORM_THROUGHPUT_DETAILS_SUBTASK,
  tprFormQuery,
  validateZeroEnergyForThroughput,
} from '@requests/common';

export const tprThroughputRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(tprFormQuery.selectSectionsCompleted)() ?? {};
  const sectionStatus = sectionsCompleted[TPR_FORM_THROUGHPUT_DETAILS_SUBTASK];

  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);
  if (sectionStatus === TaskItemStatus.IN_PROGRESS && isThroughputDataConfirmable(store)) {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  return createUrlTreeFromSnapshot(route, ['details']);
};

function isThroughputDataConfirmable(store: RequestTaskStore): boolean {
  const performanceData = store.select(tprFormQuery.selectPerformanceData)();
  const baselineAndTargets = store.select(tprFormQuery.selectReferenceData)()?.baselineAndTargets;

  return validateZeroEnergyForThroughput(performanceData?.energyFuelDetails, baselineAndTargets) === null;
}
