import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { RequestedDocuments } from 'cca-api';

import { preAuditReviewQuery } from '../../pre-audit-review.selectors';
import { PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_SUBTASK } from '../../types';

export const preAuditReviewRequestedDocumentsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const sectionsCompleted = store.select(preAuditReviewQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_SUBTASK];

  const requestedDocuments = store.select(preAuditReviewQuery.selectPreAuditReviewDetails)()?.requestedDocuments;

  const completed = requestedDocuments?.auditMaterialReceivedDate && atLeastOneDocumentUploaded(requestedDocuments);

  if (!completed) return createUrlTreeFromSnapshot(route, ['upload-documents']);
  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};

function atLeastOneDocumentUploaded(requestedDocuments: RequestedDocuments): boolean {
  return (
    !!requestedDocuments?.processFlowMapsFile ||
    !!requestedDocuments?.manufacturingProcessFile ||
    !!requestedDocuments?.annotatedSitePlansFile ||
    !!requestedDocuments?.eligibleProcessFile ||
    !!requestedDocuments?.directlyAssociatedActivitiesFile ||
    !!requestedDocuments?.seventyPerCentRuleEvidenceFile ||
    requestedDocuments?.baseYearTargetPeriodEvidenceFiles?.length > 0 ||
    requestedDocuments?.additionalDocuments?.length > 0
  );
}
