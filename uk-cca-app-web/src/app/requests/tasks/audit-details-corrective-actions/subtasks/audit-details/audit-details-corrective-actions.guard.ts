import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { AuditDetails } from 'cca-api';

import { auditDetailsCorrectiveActionsQuery } from '../../audit-details-corrective-actions.selectors';
import { AUDIT_DETAILS_SUBTASK } from '../../types';

export const auditDetailsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const sectionsCompleted = store.select(auditDetailsCorrectiveActionsQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[AUDIT_DETAILS_SUBTASK];

  const auditDetails = store.select(auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions)()
    ?.auditDetails;

  if (!isWizardCompleted(auditDetails)) return createUrlTreeFromSnapshot(route, ['details']);
  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};

function isWizardCompleted(auditDetails: AuditDetails): boolean {
  return (
    !!auditDetails?.auditDate &&
    !!auditDetails?.auditTechnique &&
    !!auditDetails?.comments &&
    !!auditDetails?.finalAuditReportDate &&
    auditDetails?.auditDocuments.length > 0
  );
}
