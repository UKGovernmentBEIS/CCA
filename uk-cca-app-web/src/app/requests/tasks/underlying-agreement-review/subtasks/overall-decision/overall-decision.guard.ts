import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { OverallDecisionWizardStep, underlyingAgreementReviewQuery } from '@requests/common';

export const canActivateOverallDecision: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);
  const determinationSubmitted = requestTaskStore.select(underlyingAgreementReviewQuery.selectDeterminationSubmitted)();
  if (determinationSubmitted) return createUrlTreeFromSnapshot(route, ['summary']);
  return createUrlTreeFromSnapshot(route, [OverallDecisionWizardStep.AVAILABLE_ACTIONS]);
};
