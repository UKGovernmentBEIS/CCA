import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  BaselineAndTargetPeriodsSubtasks,
  isTargetPeriodWizardCompleted,
  TaskItemStatus,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';

export const tp5RedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const store = inject(RequestTaskStore);

  const targetPeriodDetails = store.select(underlyingAgreementQuery.selectTargetPeriodDetails(true))();
  const baselineExists = store.select(underlyingAgreementQuery.selectTargetPeriodExists)();
  const reviewSectionsCompleted = store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)();

  const reviewSectionStatus = reviewSectionsCompleted[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS];

  const statusPending =
    reviewSectionStatus !== TaskItemStatus.ACCEPTED && reviewSectionStatus !== TaskItemStatus.REJECTED;

  if (!statusPending || reviewSectionStatus === TaskItemStatus.UNCHANGED) {
    return createUrlTreeFromSnapshot(route, ['summary']);
  }

  const decision = store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD5_DETAILS'))();

  // Handle edge case: if baseline doesn't exist and no decision yet, go straight to decision
  if (baselineExists === false && !decision) return createUrlTreeFromSnapshot(route, ['decision']);

  const wizardCompleted = baselineExists === false || isTargetPeriodWizardCompleted(targetPeriodDetails);

  if (!wizardCompleted) return createUrlTreeFromSnapshot(route, ['baseline-exists']);
  if (wizardCompleted && !decision) return createUrlTreeFromSnapshot(route, ['decision']);
  if (decision && wizardCompleted) return createUrlTreeFromSnapshot(route, ['check-your-answers']);

  return false;
};
