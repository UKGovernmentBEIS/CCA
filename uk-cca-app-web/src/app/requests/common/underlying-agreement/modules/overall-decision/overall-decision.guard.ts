import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  OVERALL_DECISION_SUBTASK,
  OverallDecisionWizardStep,
  TaskItemStatus,
  underlyingAgreementReviewQuery,
} from '@requests/common';

import { isWizardCompleted } from './overall-decision.wizard';

export const canActivatePostDecisionSubtasks: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const requestTaskStore = inject(RequestTaskStore);

  const determination = requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();

  if (!determination.type) {
    return createUrlTreeFromSnapshot(route, ['../', OverallDecisionWizardStep.AVAILABLE_ACTIONS]);
  }

  return true;
};

export const canActivateOverallDecisionSubtask: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);
  const change = route.queryParamMap.get('change') === 'true';

  if (change) return true;

  const reviewSectionCompleted = requestTaskStore.select(
    underlyingAgreementReviewQuery.selectReviewSectionCompleted(OVERALL_DECISION_SUBTASK),
  )();

  if ([TaskItemStatus.APPROVED, TaskItemStatus.REJECTED].includes(reviewSectionCompleted)) {
    return createUrlTreeFromSnapshot(route, ['../', OverallDecisionWizardStep.SUMMARY]);
  }

  const determination = requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();

  if (isWizardCompleted(determination) && reviewSectionCompleted === TaskItemStatus.UNDECIDED) {
    return createUrlTreeFromSnapshot(route, ['../', OverallDecisionWizardStep.CHECK_ANSWERS]);
  }

  return true;
};

export const canActivateOverallDecisionSummary: CanActivateFn = (route) => {
  const requestTaskStore = inject(RequestTaskStore);
  const determination = requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();

  if (isWizardCompleted(determination)) return true;

  return createUrlTreeFromSnapshot(route, ['../', OverallDecisionWizardStep.AVAILABLE_ACTIONS]);
};

export const canActivateOverallDecisionCheckYourAnswers: CanActivateFn = (route) => {
  const requestTaskStore = inject(RequestTaskStore);

  const reviewSectionCompleted = requestTaskStore.select(
    underlyingAgreementReviewQuery.selectReviewSectionCompleted(OVERALL_DECISION_SUBTASK),
  )();

  if ([TaskItemStatus.APPROVED, TaskItemStatus.REJECTED].includes(reviewSectionCompleted)) {
    return createUrlTreeFromSnapshot(route, ['../', OverallDecisionWizardStep.SUMMARY]);
  }

  const determination = requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();
  if (isWizardCompleted(determination)) return true;

  return createUrlTreeFromSnapshot(route, ['../', OverallDecisionWizardStep.AVAILABLE_ACTIONS]);
};
