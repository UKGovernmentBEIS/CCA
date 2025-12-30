import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK } from '../../types';
import { adminTerminationWithdrawQuery } from '../../withdraw-admin-termination.selectors';

export const reasonForWithdrawAdminTerminationRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const reasonDetails = store.select(adminTerminationWithdrawQuery.selectReasonDetails)();
  if (!reasonDetails?.explanation) return createUrlTreeFromSnapshot(route, ['reason-details']);

  const sectionsCompleted = store.select(adminTerminationWithdrawQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK];

  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
