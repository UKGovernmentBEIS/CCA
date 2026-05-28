import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { NonComplianceConclusion } from 'cca-api';

import { nonComplianceConclusionQuery } from './non-compliance-conclusion.selectors';
import { NON_COMPLIANCE_CONCLUSION_SUBTASK } from './types';

export const nonComplianceConclusionRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const sectionsCompleted = store.select(nonComplianceConclusionQuery.selectSectionsCompleted)() ?? {};
  const status = sectionsCompleted[NON_COMPLIANCE_CONCLUSION_SUBTASK];

  if (status === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  const conclusion = store.select(nonComplianceConclusionQuery.selectNonComplianceConclusion)();

  if (!conclusion?.details?.penaltyOutcome) {
    return createUrlTreeFromSnapshot(route, ['provide-details']);
  }

  if (conclusion.details.penaltyOutcome === 'WITHDRAW' && !conclusion.withdrawNotice?.file) {
    return createUrlTreeFromSnapshot(route, ['provide-withdrawal-notice']);
  }

  if (isConclusionWizardCompleted(conclusion)) {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  return createUrlTreeFromSnapshot(route, ['provide-details']);
};

export const nonComplianceConclusionEditableGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();

  if (isEditable) return true;

  return createUrlTreeFromSnapshot(route.parent ?? route, ['summary']);
};

export function isConclusionWizardCompleted(conclusion: NonComplianceConclusion): boolean {
  if (!conclusion?.details) return false;
  const d = conclusion.details;

  const hasRequiredFields = d.complianceRestored != null && d.penaltyPaid != null && !!d.comment && !!d.penaltyOutcome;

  if (!hasRequiredFields) return false;

  if (d.complianceRestored && !d.complianceRestoredDate) return false;
  if (d.penaltyPaid && !d.penaltyPaymentDate) return false;

  if (d.penaltyOutcome === 'WITHDRAW') {
    if (!conclusion.withdrawNotice?.file) return false;
    if (!conclusion.withdrawNotice?.comments) return false;
  }
  return true;
}
