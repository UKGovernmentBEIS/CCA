import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { REVIEW_TARGET_UNIT_DETAILS_SUBTASK, TaskItemStatus, underlyingAgreementQuery } from '@requests/common';

export const reviewTargetUnitDetailsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK];

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);
  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
