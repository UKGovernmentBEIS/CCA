import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';

import { underlyingAgreementActivationQuery } from '../../una-activation.selectors';

export const unaActivationRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const sectionsCompleted = store.select(underlyingAgreementActivationQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[PROVIDE_EVIDENCE_SUBTASK];

  const details = store.select(underlyingAgreementActivationQuery.selectDetails)();
  if (!details) return createUrlTreeFromSnapshot(route, ['details']);

  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
