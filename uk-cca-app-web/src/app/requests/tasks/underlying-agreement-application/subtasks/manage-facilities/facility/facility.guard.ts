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
  const facilityId = route.parent?.paramMap.get('facilityId');

  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[facilityId];

  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  const statusPending =
    !sectionStatus || sectionStatus === TaskItemStatus.NOT_STARTED || sectionStatus === TaskItemStatus.IN_PROGRESS;

  const facility = store.select(underlyingAgreementQuery.selectFacility(facilityId))();
  const completed = isFacilityWizardCompleted(facility);

  if (statusPending && completed) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (statusPending && !completed) return createUrlTreeFromSnapshot(route, [FacilityWizardStep.DETAILS]);

  return false;
};
