import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { CorrectiveActions } from 'cca-api';

import { auditDetailsCorrectiveActionsQuery } from '../../audit-details-corrective-actions.selectors';
import { CORRECTIVE_ACTIONS_SUBTASK } from '../../types';

export const correctiveActionsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const sectionsCompleted = store.select(auditDetailsCorrectiveActionsQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[CORRECTIVE_ACTIONS_SUBTASK];

  const correctiveActions = store.select(auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions)()
    ?.correctiveActions;

  if (!isWizardCompleted(correctiveActions)) return createUrlTreeFromSnapshot(route, ['has-actions']);
  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};

function isWizardCompleted(correctiveActions: CorrectiveActions): boolean {
  if (correctiveActions?.hasActions === false) return true;
  return correctiveActions?.hasActions && correctiveActions?.actions.length > 0;
}
