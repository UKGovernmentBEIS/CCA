import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { preAuditReviewQuery } from '../../pre-audit-review.selectors';
import { PRE_AUDIT_REVIEW_DETERMINATION_SUBTASK } from '../../types';

export const preAuditReviewDeterminationRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const sectionsCompleted = store.select(preAuditReviewQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[PRE_AUDIT_REVIEW_DETERMINATION_SUBTASK];

  const auditDetermination = store.select(preAuditReviewQuery.selectPreAuditReviewDetails)()?.auditDetermination;

  const completed = !!auditDetermination?.reviewCompletionDate && auditDetermination?.furtherAuditNeeded !== null;

  if (!completed) return createUrlTreeFromSnapshot(route, ['review-determination']);
  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
