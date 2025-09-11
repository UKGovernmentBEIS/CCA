import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK, underlyingAgreementReviewQuery } from '@requests/common';

export const authorisationAdditionalEvidenceRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const reviewSectionCompleted = store.select(
    underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK),
  )();

  const decision = store.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'),
  )();

  if (!reviewSectionCompleted && !decision) return createUrlTreeFromSnapshot(route, ['decision']);
  if (!reviewSectionCompleted && decision) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (reviewSectionCompleted && decision) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
