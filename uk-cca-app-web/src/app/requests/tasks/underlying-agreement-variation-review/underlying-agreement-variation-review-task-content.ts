import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  overallDecisionStatus,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  UNAVariationReviewRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

import { UnderlyingAgreementVariationReviewPrecontentComponent } from './precontent/underlying-agreement-variation-review-precontent.component';
import { calcManageFacilitiesStatus, reviewSectionsCompleted } from './utils';

const routePrefix = 'underlying-agreement-variation-review';

export const underlyingAgreementVariationReviewTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(requestTaskQuery.selectRequestTaskPayload)();

  return {
    header: 'Review underlying agreement variation',
    preContentComponent: UnderlyingAgreementVariationReviewPrecontentComponent,
    sections: getAllUnderlyingAgreementVariationSections(payload),
  };
};

export function getAllUnderlyingAgreementVariationSections(
  payload: UNAVariationReviewRequestTaskPayload,
): TaskSection[] {
  const sections: TaskSection[] = [
    {
      title: 'Variation details',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted[VARIATION_DETAILS_SUBTASK] ?? TaskItemStatus.UNCHANGED,
          link: `${routePrefix}/variation-details`,
          linkText: 'Describe the changes',
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] ?? TaskItemStatus.UNCHANGED,
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
  ];

  if (payload?.underlyingAgreement?.targetPeriod5Details && payload?.underlyingAgreement?.targetPeriod6Details) {
    sections.push({
      title: 'Baseline and Targets',
      tasks: [
        {
          status:
            payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] ??
            TaskItemStatus.UNCHANGED,
          link: `${routePrefix}/target-period-5`,
          linkText: 'TP5 (2021-2022)',
        },
        {
          status:
            payload?.reviewSectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] ??
            TaskItemStatus.UNCHANGED,
          link: `${routePrefix}/target-period-6`,
          linkText: 'TP6 (2024)',
        },
      ],
    });
  }

  sections.push(
    {
      title: 'Authorisation details',
      tasks: [
        {
          status:
            payload?.reviewSectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] ?? TaskItemStatus.UNCHANGED,
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
  );

  return sections;
}
