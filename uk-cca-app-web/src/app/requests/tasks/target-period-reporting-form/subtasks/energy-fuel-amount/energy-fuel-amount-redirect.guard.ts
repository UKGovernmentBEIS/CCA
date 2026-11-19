import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  isEnergyFuelDataConfirmable,
  TaskItemStatus,
  TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK,
  tprFormQuery,
} from '@requests/common';

export const energyFuelAmountRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(tprFormQuery.selectSectionsCompleted)() ?? {};
  const sectionStatus = sectionsCompleted[TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK];
  const isConfirmable = isEnergyFuelDataConfirmable(
    store.select(tprFormQuery.selectPerformanceData)()?.energyFuelDetails,
    store.select(tprFormQuery.selectReferenceData)()?.baselineAndTargets?.usedReportingMechanism,
  );

  if (sectionStatus === TaskItemStatus.COMPLETED && isConfirmable) return createUrlTreeFromSnapshot(route, ['summary']);
  if (sectionStatus === TaskItemStatus.IN_PROGRESS && isConfirmable) {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  return createUrlTreeFromSnapshot(route, ['details']);
};
