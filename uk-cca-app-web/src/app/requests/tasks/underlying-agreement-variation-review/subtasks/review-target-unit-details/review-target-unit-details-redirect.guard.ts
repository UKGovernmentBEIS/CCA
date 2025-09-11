import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { REVIEW_TARGET_UNIT_DETAILS_SUBTASK, underlyingAgreementReviewQuery } from '@requests/common';

export const reviewTargetUnitDetailsRedirectGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const store = inject(RequestTaskStore);

  const reviewSectionCompleted = store.select(
    underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(REVIEW_TARGET_UNIT_DETAILS_SUBTASK),
  )();

  if (reviewSectionCompleted) return createUrlTreeFromSnapshot(route, ['summary']);

  const decision = store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'))();

  return decision
    ? createUrlTreeFromSnapshot(route, ['check-your-answers'])
    : createUrlTreeFromSnapshot(route, ['decision']);
};
