import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  FacilityWizardStep,
  isFacilityWizardCompleted,
  TaskItemStatus,
  underlyingAgreementQuery,
} from '@requests/common';

export const facilityRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const facilityId = route.params.facilityId;

  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const facilityStatus = sectionsCompleted[facilityId];

  if (facilityStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  const statusPending =
    !facilityStatus || facilityStatus === TaskItemStatus.NOT_STARTED || facilityStatus === TaskItemStatus.IN_PROGRESS;

  const facility = store.select(underlyingAgreementQuery.selectFacility(facilityId))();
  const completed = isFacilityWizardCompleted(facility);

  if (statusPending && completed) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (statusPending && !completed) return createUrlTreeFromSnapshot(route, [FacilityWizardStep.DETAILS]);

  return false;
};
