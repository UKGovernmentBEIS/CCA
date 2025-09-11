import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  isTargetPeriodWizardCompleted,
  TaskItemStatus,
  underlyingAgreementQuery,
} from '@requests/common';

export const tp5RedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS];
  const statusPending = sectionStatus === TaskItemStatus.NOT_STARTED || sectionStatus === TaskItemStatus.IN_PROGRESS;
  const baselineExists = store.select(underlyingAgreementQuery.selectTargetPeriodExists)();
  const targetPeriodDetails = store.select(underlyingAgreementQuery.selectTargetPeriodDetails(true))();
  const completed = baselineExists === false || isTargetPeriodWizardCompleted(targetPeriodDetails);

  if (statusPending && !completed) return createUrlTreeFromSnapshot(route, [BaseLineAndTargetsStep.BASELINE_EXISTS]);
  if (statusPending && completed) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
