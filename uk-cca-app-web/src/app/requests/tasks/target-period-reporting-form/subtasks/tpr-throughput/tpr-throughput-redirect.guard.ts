import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  isThroughputDataConfirmable,
  TaskItemStatus,
  TPR_FORM_THROUGHPUT_DETAILS_SUBTASK,
  tprFormQuery,
} from '@requests/common';

export const tprThroughputRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(tprFormQuery.selectSectionsCompleted)() ?? {};
  const sectionStatus = sectionsCompleted[TPR_FORM_THROUGHPUT_DETAILS_SUBTASK];
  const isConfirmable = isThroughputDataConfirmable(
    store.select(tprFormQuery.selectReferenceData)()?.baselineAndTargets,
    store.select(tprFormQuery.selectPerformanceData)(),
  );

  if (sectionStatus === TaskItemStatus.COMPLETED && isConfirmable) return createUrlTreeFromSnapshot(route, ['summary']);
  if (sectionStatus === TaskItemStatus.IN_PROGRESS && isConfirmable) {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  return createUrlTreeFromSnapshot(route, ['details']);
};
