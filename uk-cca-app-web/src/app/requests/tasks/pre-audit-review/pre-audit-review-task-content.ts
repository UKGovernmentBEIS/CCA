import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';
import { produce } from 'immer';
import { RequestTaskPageContentFactory } from 'projects/common/request-task';

import { PreAuditReviewSubmitRequestTaskPayload } from 'cca-api';

import { CompleteTaskButtonComponent } from './complete-task/complete-task-button.component';
import { preAuditReviewQuery } from './pre-audit-review.selectors';
import {
  PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK,
  PRE_AUDIT_REVIEW_DETERMINATION_SUBTASK,
  PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_SUBTASK,
} from './types';

const routePrefix = 'pre-audit-review';

export const preAuditReviewTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(preAuditReviewQuery.selectPayload)();

  return {
    header: 'Pre-audit review',
    preContentComponent: CompleteTaskButtonComponent,
    sections: preAuditReviewSections(payload),
  };
};

function preAuditReviewSections(payload: PreAuditReviewSubmitRequestTaskPayload): TaskSection[] {
  return [
    {
      title: 'Pre-audit review reason',
      tasks: [
        {
          status: calculateReviewReasonStatus(payload),
          link: `${routePrefix}/reason`,
          linkText: 'Describe the reason for pre-audit review',
        },
      ],
    },
    {
      title: 'Requested documents',
      tasks: [
        {
          status:
            payload?.sectionsCompleted[PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/requested-documents`,
          linkText: 'Upload documents',
        },
      ],
    },
    {
      title: 'Pre-audit review determination',
      tasks: [
        {
          status: payload?.sectionsCompleted[PRE_AUDIT_REVIEW_DETERMINATION_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/determination`,
          linkText: 'Pre-audit review determination',
        },
      ],
    },
  ];
}

function calculateReviewReasonStatus(payload: PreAuditReviewSubmitRequestTaskPayload): TaskItemStatus {
  const auditReasonDetails = payload?.preAuditReviewDetails?.auditReasonDetails;
  const completed = auditReasonDetails?.reasonsForAudit?.length > 0 && auditReasonDetails?.comment?.length > 0;

  if (completed && payload?.sectionsCompleted[PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK] === TaskItemStatus.NOT_STARTED) {
    const sectionsCompleted = produce(payload?.sectionsCompleted, (draft) => {
      draft[PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    return sectionsCompleted[PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK] as TaskItemStatus;
  }

  return (
    (payload?.sectionsCompleted[PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK] as TaskItemStatus) ?? TaskItemStatus.NOT_STARTED
  );
}
