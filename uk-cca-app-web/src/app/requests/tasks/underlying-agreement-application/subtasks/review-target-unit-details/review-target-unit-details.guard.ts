import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  TaskItemStatus,
  underlyingAgreementQuery,
} from '@requests/common';

import { UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

export const reviewTargetUnitDetailsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK];

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const tuDetails = (
    store.select(requestTaskQuery.selectRequestTaskPayload)() as UnderlyingAgreementSubmitRequestTaskPayload
  )?.underlyingAgreement?.underlyingAgreementTargetUnitDetails;

  const completed = isTargetUnitDetailsWizardCompleted(tuDetails);

  if (!completed) {
    return createUrlTreeFromSnapshot(route, [ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER]);
  }

  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
