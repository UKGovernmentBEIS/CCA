import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus, underlyingAgreementReviewQuery, VARIATION_DETAILS_SUBTASK } from '@requests/common';

export const variationDetailsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const store = inject(RequestTaskStore);

  const reviewSectionsCompleted = store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)();
  const reviewSectionStatus = reviewSectionsCompleted[VARIATION_DETAILS_SUBTASK];

  const statusPending =
    reviewSectionStatus !== TaskItemStatus.ACCEPTED && reviewSectionStatus !== TaskItemStatus.REJECTED;

  const decision = store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('VARIATION_DETAILS'))();

  if (!statusPending) return createUrlTreeFromSnapshot(route, ['summary']);
  if (!decision) return createUrlTreeFromSnapshot(route, ['decision']);

  return createUrlTreeFromSnapshot(route, ['check-your-answers']);
};
