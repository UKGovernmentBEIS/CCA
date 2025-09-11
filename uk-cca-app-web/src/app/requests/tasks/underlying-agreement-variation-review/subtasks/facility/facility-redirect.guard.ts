import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  FacilityWizardStep,
  isFacilityWizardCompleted,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';

export const facilityRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const store = inject(RequestTaskStore);

  const facilityId = route.params.facilityId;

  const reviewSectionCompleted = store.select(
    underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(facilityId),
  )();
  if (reviewSectionCompleted) return createUrlTreeFromSnapshot(route, ['summary']);

  const facility = store.select(underlyingAgreementQuery.selectFacility(facilityId))();
  const completed = isFacilityWizardCompleted(facility);
  if (!completed) return createUrlTreeFromSnapshot(route, [FacilityWizardStep.DETAILS]);

  const decision = store.select(underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(facilityId))();
  return decision
    ? createUrlTreeFromSnapshot(route, ['check-your-answers'])
    : createUrlTreeFromSnapshot(route, ['decision']);
};
