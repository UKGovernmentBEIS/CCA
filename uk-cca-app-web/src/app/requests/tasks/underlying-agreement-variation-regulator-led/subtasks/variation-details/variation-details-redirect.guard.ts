import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

import { UnderlyingAgreementVariationDetails } from 'cca-api';

export const variationDetailsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const store = inject(RequestTaskStore);
  // Get current state
  const variationDetails = store.select(underlyingAgreementVariationQuery.selectVariationDetails)();
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();

  // Determine where to redirect
  if (!isWizardCompleted(variationDetails)) {
    return createUrlTreeFromSnapshot(route, ['variation-details']);
  }

  const sectionStatus = sectionsCompleted[VARIATION_DETAILS_SUBTASK];
  if (sectionStatus === 'COMPLETED') {
    return createUrlTreeFromSnapshot(route, ['summary']);
  } else if (sectionStatus === 'IN_PROGRESS') {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  // Default to details page
  return createUrlTreeFromSnapshot(route, ['variation-details']);
};

function isWizardCompleted(variationDetails: UnderlyingAgreementVariationDetails) {
  return !!variationDetails;
}
