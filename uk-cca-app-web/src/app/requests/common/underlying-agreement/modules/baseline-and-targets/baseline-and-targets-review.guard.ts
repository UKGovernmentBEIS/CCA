import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { underlyingAgreementQuery, underlyingAgreementReviewQuery } from '../../+state';
import { BaselineAndTargetPeriodsSubtasks, BaseLineAndTargetsReviewStep } from '../../underlying-agreement.types';
import { isTargetPeriodWizardCompleted } from './baseline-and-targets-wizard';

export const canActivateTargetPeriodReview = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const targetPerioDetails = store.select(underlyingAgreementQuery.selectTargetPeriodDetails(true))();
  const baselineExists = store.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsReviewStep.SUMMARY]);

  if (!isTargetPeriodWizardCompleted(true, baselineExists, targetPerioDetails)) return true;

  const change = route.queryParamMap.get('change') === 'true';
  if (change) return true;

  const reviewSectionCompleted = store.select(
    underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(
      BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
    ),
  )();

  const decision = store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD5_DETAILS'))();

  if (!decision) return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsReviewStep.DECISION]);

  if (!reviewSectionCompleted)
    return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS]);

  return false;
};

export const canActivateTargetPeriodDecision = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsReviewStep.SUMMARY]);

  const targetPerioDetails = store.select(underlyingAgreementQuery.selectTargetPeriodDetails(true))();
  const baselineExists = store.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  if (!isTargetPeriodWizardCompleted(true, baselineExists, targetPerioDetails))
    return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsReviewStep.BASELINE_EXISTS]);

  const change = route.queryParamMap.get('change') === 'true';
  if (change) return true;

  const reviewSectionCompleted = store.select(
    underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(
      BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
    ),
  )();

  const hasDecision = store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD5_DETAILS'))();

  if (reviewSectionCompleted) return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsReviewStep.SUMMARY]);

  if (hasDecision) return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS]);

  return true;
};
