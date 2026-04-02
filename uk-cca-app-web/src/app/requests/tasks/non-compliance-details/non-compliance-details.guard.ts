import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { NonComplianceDetails } from 'cca-api';

import { nonComplianceDetailsQuery } from './non-compliance-details.selectors';
import { NON_COMPLIANCE_DETAILS_SUBTASK } from './types';

export const nonComplianceDetailsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable) {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  const sectionsCompleted = store.select(nonComplianceDetailsQuery.selectSectionsCompleted)() ?? {};
  const status = sectionsCompleted[NON_COMPLIANCE_DETAILS_SUBTASK];
  const details = store.select(nonComplianceDetailsQuery.selectNonComplianceDetails)();
  const wizardCompleted = isNonComplianceWizardCompleted(details);

  if (status === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  if (!status || status === TaskItemStatus.NOT_STARTED || status === TaskItemStatus.IN_PROGRESS) {
    if (wizardCompleted) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
    return createUrlTreeFromSnapshot(route, ['provide-details']);
  }

  return false;
};

export const nonComplianceDetailsEditableGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();

  if (isEditable) {
    return true;
  }

  return createUrlTreeFromSnapshot(route.parent ?? route, ['check-your-answers']);
};

export function isNonComplianceWizardCompleted(details: NonComplianceDetails): boolean {
  if (!details) return false;
  if (!details.nonComplianceType) return false;
  if (details.isEnforcementResponseNoticeRequired == null) return false;
  if (details.isEnforcementResponseNoticeRequired === false && !details.explanation) return false;
  return true;
}
