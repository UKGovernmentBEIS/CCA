import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  FacilityWizardStep,
  isFacilityWizardCompleted,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';

export const facilityRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const facilityId = route.params.facilityId;
  const facility = store.select(underlyingAgreementQuery.selectFacility(facilityId))();

  const completed = isFacilityWizardCompleted(facility);
  if (!completed) return createUrlTreeFromSnapshot(route, [FacilityWizardStep.DETAILS]);

  const decision = store.select(underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(facilityId))();
  if (completed && !decision) return createUrlTreeFromSnapshot(route, ['decision']);

  const reviewSectionCompleted = store.select(
    underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(facilityId),
  )();

  if (decision && completed && !reviewSectionCompleted) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (reviewSectionCompleted && decision) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
