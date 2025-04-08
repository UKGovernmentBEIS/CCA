import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  overallDecisionStatus,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  transformFacilities,
} from '@requests/common';

import { UnderlyingAgreementReviewRequestTaskPayload } from 'cca-api';

import { UnderlyingAgreementReviewPrecontentComponent } from './precontent/underlying-agreement-review-precontent.component';
import { reviewSectionsCompleted } from './utils';

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
      tasks: transformFacilities(
        payload?.underlyingAgreement?.facilities,
        [],
        payload?.reviewSectionsCompleted,
        routePrefix,
        '',
        TaskItemStatus.UNDECIDED,
      ),
    },
    {
      title: 'Baseline and Targets',
      tasks: [
        {
          status:
            payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] ??
            TaskItemStatus.UNDECIDED,
          link: `${routePrefix}/target-period-5`,
          linkText: 'TP5 (2021-2022)',
        },
        {
          status:
            payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] ??
            TaskItemStatus.UNDECIDED,
          link: `${routePrefix}/target-period-6`,
          linkText: 'TP6 (2024)',
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
