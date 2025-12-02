import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  underlyingAgreementReviewQuery,
} from '@requests/common';

import { UnderlyingAgreementVariationReviewRequestTaskPayload } from 'cca-api';

export const reviewTargetUnitDetailsRedirectGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const store = inject(RequestTaskStore);

  const reviewSectionCompleted = store.select(
    underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(REVIEW_TARGET_UNIT_DETAILS_SUBTASK),
  )();

  if (reviewSectionCompleted) return createUrlTreeFromSnapshot(route, ['summary']);

  const payload = store.select(
    requestTaskQuery.selectRequestTaskPayload,
  )() as UnderlyingAgreementVariationReviewRequestTaskPayload;

  if (!isTargetUnitDetailsWizardCompleted(payload.underlyingAgreement?.underlyingAgreementTargetUnitDetails)) {
    return createUrlTreeFromSnapshot(route, [ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER]);
  }

  const decision = store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'))();

  return decision
    ? createUrlTreeFromSnapshot(route, ['check-your-answers'])
    : createUrlTreeFromSnapshot(route, ['decision']);
};
