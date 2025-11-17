import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK, underlyingAgreementReviewQuery } from '@requests/common';

export const authorisationAdditionalEvidenceRedirectGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const store = inject(RequestTaskStore);

  const reviewSectionCompleted = store.select(
    underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK),
  )();
  if (reviewSectionCompleted) return createUrlTreeFromSnapshot(route, ['summary']);

  const decision = store.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'),
  )();

  return decision
    ? createUrlTreeFromSnapshot(route, ['check-your-answers'])
    : createUrlTreeFromSnapshot(route, ['decision']);
};
