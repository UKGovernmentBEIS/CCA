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
  UNAVariationReviewRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

import { UnderlyingAgreementVariationReviewPrecontentComponent } from './precontent/underlying-agreement-variation-review-precontent.component';
import { reviewSectionsCompleted } from './utils';

const routePrefix = 'underlying-agreement-variation-review';

export const underlyingAgreementVariationReviewTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);

  return {
    header: 'Review underlying agreement variation',
    preContentComponent: UnderlyingAgreementVariationReviewPrecontentComponent,
    sections: getAllUnderlyingAgreementVariationSections(store.state?.requestTaskItem?.requestTask?.payload),
  };
};

export function getAllUnderlyingAgreementVariationSections(
  payload: UNAVariationReviewRequestTaskPayload,
): TaskSection[] {
  const facilities = transformFacilities(
    payload?.underlyingAgreement?.facilities,
    ['NEW', 'LIVE'],
    payload?.reviewSectionsCompleted,
    routePrefix,
    'summary',
    TaskItemStatus.UNDECIDED,
  );

  const excludedFacilities = transformFacilities(
    payload?.underlyingAgreement?.facilities,
    ['EXCLUDED'],
    payload?.reviewSectionsCompleted,
    routePrefix,
    'summary',
    TaskItemStatus.UNDECIDED,
  );

  return [
    {
      title: 'Variation details',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted[VARIATION_DETAILS_SUBTASK] ?? TaskItemStatus.UNDECIDED,
          link: `${routePrefix}/variation-details`,
          linkText: 'Describe the changes',
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload?.reviewSectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] ?? TaskItemStatus.UNDECIDED,
          link: `${routePrefix}/review-target-unit-details`,
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: facilities,
    },
    {
      title: 'Excluded facilities',
      tasks: excludedFacilities,
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
  ].filter((item) => item.tasks.length > 0);
}
