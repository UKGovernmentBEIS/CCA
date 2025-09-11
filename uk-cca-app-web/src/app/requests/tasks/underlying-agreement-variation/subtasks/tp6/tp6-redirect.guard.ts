import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { BaselineAndTargetPeriodsSubtasks, TaskItemStatus, underlyingAgreementQuery } from '@requests/common';

export const tp6RedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const store = inject(RequestTaskStore);

  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS];
  const statusPending = sectionStatus === TaskItemStatus.NOT_STARTED || sectionStatus === TaskItemStatus.IN_PROGRESS;

  if (statusPending) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  return createUrlTreeFromSnapshot(route, ['summary']);
};
