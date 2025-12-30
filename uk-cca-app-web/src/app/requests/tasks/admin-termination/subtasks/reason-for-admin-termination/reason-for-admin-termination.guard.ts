import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { adminTerminationQuery } from '../../admin-termination.selectors';
import { REASON_FOR_ADMIN_TERMINATION_SUBTASK } from '../../types';
import { isWizardCompleted } from './completed';

export const reasonForAdminTerminationRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const sectionsCompleted = store.select(adminTerminationQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[REASON_FOR_ADMIN_TERMINATION_SUBTASK];

  const reasonDetails = store.select(adminTerminationQuery.selectReasonDetails)();
  const completed = isWizardCompleted(reasonDetails);

  if (!completed) return createUrlTreeFromSnapshot(route, ['reason-details']);
  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
