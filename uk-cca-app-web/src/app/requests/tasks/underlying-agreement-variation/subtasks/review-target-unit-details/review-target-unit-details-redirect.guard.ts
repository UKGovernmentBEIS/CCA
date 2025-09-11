import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  underlyingAgreementQuery,
} from '@requests/common';

export const reviewTargetUnitDetailsRedirectGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const store = inject(RequestTaskStore);

  // Get current state
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK];

  // Determine where to redirect based on section status
  if (sectionStatus === 'COMPLETED') {
    return createUrlTreeFromSnapshot(route, ['summary']);
  } else if (sectionStatus === 'IN_PROGRESS') {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  // Default to first step in the wizard
  return createUrlTreeFromSnapshot(route, [ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS]);
};
