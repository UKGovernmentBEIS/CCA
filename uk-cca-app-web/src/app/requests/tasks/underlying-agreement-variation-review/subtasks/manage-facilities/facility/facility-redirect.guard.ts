import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  FacilityWizardStep,
  isFacilityWizardCompleted,
  TaskItemStatus,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';

export const facilityRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const facilityId = route.params.facilityId;
  const facility = store.select(underlyingAgreementQuery.selectFacility(facilityId))();

  const reviewSectionStatus = store.select(underlyingAgreementReviewQuery.selectReviewSectionCompleted(facilityId))();

  const statusPending =
    reviewSectionStatus !== TaskItemStatus.ACCEPTED && reviewSectionStatus !== TaskItemStatus.REJECTED;
  const unchangedStatus = reviewSectionStatus === TaskItemStatus.UNCHANGED;

  if (!statusPending || unchangedStatus) {
    return createUrlTreeFromSnapshot(route, ['summary']);
  }

  const completed = isFacilityWizardCompleted(facility);
  if (!completed) return createUrlTreeFromSnapshot(route, [FacilityWizardStep.DETAILS]);

  const decision = store.select(underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(facilityId))();
  if (!decision && !unchangedStatus) return createUrlTreeFromSnapshot(route, ['decision']);
  if (decision || unchangedStatus) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
};
