import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { OverallDecisionWizardStep, underlyingAgreementReviewQuery } from '@requests/common';

import { OverallDecisionStore } from './overall-decision.store';
import { isWizardCompleted } from './overall-decision.wizard';

export const canActivateOverallDecision: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const change = route.queryParamMap.get('change') === 'true';
  if (change) return true;
  const determination = inject(OverallDecisionStore).determination;
  if (!determination.type)
    return createUrlTreeFromSnapshot(route, ['../', OverallDecisionWizardStep.AVAILABLE_ACTIONS]);
  return true;
};

export const canActivateOverallDecisionSummary: CanActivateFn = (route) => {
  const determination = inject(OverallDecisionStore).determination;
  if (isWizardCompleted(determination)) return true;
  return createUrlTreeFromSnapshot(route, ['../', OverallDecisionWizardStep.AVAILABLE_ACTIONS]);
};

export const initializeOverallDecisionStore: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const overallDecisionStore = inject(OverallDecisionStore);
  const determination = store.select(underlyingAgreementReviewQuery.selectDetermination)();
  overallDecisionStore.updateDetermination(determination);
  return true;
};
