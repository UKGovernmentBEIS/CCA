import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { REVIEW_TARGET_UNIT_DETAILS_SUBTASK, underlyingAgreementReviewQuery } from '@requests/common';

export const reviewTargetUnitDetailsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const reviewSectionCompleted = store.select(
    underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(REVIEW_TARGET_UNIT_DETAILS_SUBTASK),
  )();

  const decision = store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'))();

  if (!reviewSectionCompleted && !decision) return createUrlTreeFromSnapshot(route, ['decision']);
  if (!reviewSectionCompleted && decision) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (reviewSectionCompleted && decision) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
