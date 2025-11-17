import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { BaselineAndTargetPeriodsSubtasks, TaskItemStatus, underlyingAgreementReviewQuery } from '@requests/common';

export const tp6RedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const store = inject(RequestTaskStore);

  const reviewSectionsCompleted = store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)();

  const reviewSectionStatus = reviewSectionsCompleted[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS];

  const statusPending =
    reviewSectionStatus !== TaskItemStatus.ACCEPTED && reviewSectionStatus !== TaskItemStatus.REJECTED;

  if (!statusPending || reviewSectionStatus === TaskItemStatus.UNCHANGED) {
    return createUrlTreeFromSnapshot(route, ['summary']);
  }

  const decision = store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD6_DETAILS'))();

  return decision
    ? createUrlTreeFromSnapshot(route, ['check-your-answers'])
    : createUrlTreeFromSnapshot(route, ['decision']);
};
