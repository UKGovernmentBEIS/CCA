import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  calcManageFacilitiesStatus,
  overallDecisionStatus,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
} from '@requests/common';

import { UnderlyingAgreementReviewRequestTaskPayload } from 'cca-api';

import { UnderlyingAgreementReviewPrecontentComponent } from './precontent/underlying-agreement-review-precontent.component';
import { reviewSectionsCompleted } from './utils';

export const UPLOAD_DECISION_ATTACHMENT_TYPE = {
  UNDERLYING_AGREEMENT_REVIEW: 'DECISION',
  UNDERLYING_AGREEMENT_VARIATION_REVIEW: 'DECISION',
};

const routePrefix = 'underlying-agreement-review';

export const underlyingAgreementReviewTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);

  return {
    header: 'Review application for underlying agreement',
    preContentComponent: UnderlyingAgreementReviewPrecontentComponent,
    sections: getAllUnderlyingAgreementSections(store.state?.requestTaskItem?.requestTask?.payload),
  };
};

export function getAllUnderlyingAgreementSections(payload: UnderlyingAgreementReviewRequestTaskPayload): TaskSection[] {
  return [
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload.reviewSectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] ?? TaskItemStatus.UNDECIDED,
          link: `${routePrefix}/review-target-unit-details`,
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: calcManageFacilitiesStatus(payload.reviewSectionsCompleted),
          link: `${routePrefix}/manage-facilities`,
          linkText: 'Manage facilities',
        },
      ],
    },
    {
      title: 'Authorisation details',
      tasks: [
        {
          status:
            payload?.reviewSectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] ?? TaskItemStatus.UNDECIDED,
          link: `${routePrefix}/authorisation-additional-evidence`,
          linkText: 'Authorisation and additional evidence',
        },
      ],
    },
    {
      title: 'Decision',
      tasks: [
        {
          status: reviewSectionsCompleted(payload) ? overallDecisionStatus(payload) : TaskItemStatus.CANNOT_START_YET,
          link: reviewSectionsCompleted(payload) ? `${routePrefix}/send-application` : '',
          linkText: 'Overall decision',
        },
      ],
    },
  ];
}
