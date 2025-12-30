import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { adminTerminationFinalDecisionQuery } from '../../admin-termination-final-decision.selectors';
import { isWizardCompleted } from '../../completed';
import { ADMIN_TERMINATION_FINAL_DECISION_SUBTASK } from '../../types';

export const adminTerminationFinalDecisionRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const reasonDetails = store.select(adminTerminationFinalDecisionQuery.selectReasonDetails)();
  const completed = isWizardCompleted(reasonDetails);
  if (!completed) return createUrlTreeFromSnapshot(route, ['actions']);

  const sectionsCompleted = store.select(adminTerminationFinalDecisionQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK];

  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
