import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { preAuditReviewQuery } from '../../pre-audit-review.selectors';
import { PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK } from '../../types';

export const preAuditReviewAuditReasonRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const sectionsCompleted = store.select(preAuditReviewQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK];

  const auditReasonDetails = store.select(preAuditReviewQuery.selectPreAuditReviewDetails)()?.auditReasonDetails;
  const completed = auditReasonDetails?.reasonsForAudit?.length > 0 && auditReasonDetails?.comment?.length > 0;

  if (!completed || !sectionStatus || sectionStatus === TaskItemStatus.NOT_STARTED) {
    return createUrlTreeFromSnapshot(route, ['audit-reason']);
  }

  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};
