import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  underlyingAgreementReviewQuery,
} from '@requests/common';

import { UnderlyingAgreementReviewRequestTaskPayload } from 'cca-api';

export const reviewTargetUnitDetailsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const tuDetails = (
    store.select(requestTaskQuery.selectRequestTaskPayload)() as UnderlyingAgreementReviewRequestTaskPayload
  )?.underlyingAgreement?.underlyingAgreementTargetUnitDetails;

  const completed = isTargetUnitDetailsWizardCompleted(tuDetails);

  const reviewSectionCompleted = store.select(
    underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(REVIEW_TARGET_UNIT_DETAILS_SUBTASK),
  )();

  if (!completed && !reviewSectionCompleted) {
    return createUrlTreeFromSnapshot(route, [ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER]);
  }

  const decision = store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'))();

  if (!reviewSectionCompleted && !decision) return createUrlTreeFromSnapshot(route, ['decision']);
  if (!reviewSectionCompleted && decision) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (reviewSectionCompleted && decision) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
