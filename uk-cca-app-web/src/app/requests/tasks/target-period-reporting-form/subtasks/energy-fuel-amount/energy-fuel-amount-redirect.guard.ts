import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus, TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK, tprFormQuery } from '@requests/common';

export const energyFuelAmountRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(tprFormQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK];
  const statusPending = sectionStatus === TaskItemStatus.NOT_STARTED || sectionStatus === TaskItemStatus.IN_PROGRESS;

  if (statusPending) return createUrlTreeFromSnapshot(route, ['check-your-answers']);

  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return createUrlTreeFromSnapshot(route, ['details']);
};
