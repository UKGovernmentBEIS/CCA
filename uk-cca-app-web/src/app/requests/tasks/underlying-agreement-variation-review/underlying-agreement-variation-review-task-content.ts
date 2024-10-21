import { inject } from '@angular/core';

import { TaskItem, TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  UNAVariationReviewRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

import { Facility } from 'cca-api';

const routePrefix = 'underlying-agreement-variation-review';

export const underlyingAgreementVariationReviewTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);

  return {
    header: 'Review underlying agreement variation',
    sections: getAllUnderlyingAgreementVariationSections(store.state?.requestTaskItem?.requestTask?.payload),
  };
};

export function getAllUnderlyingAgreementVariationSections(
  payload: UNAVariationReviewRequestTaskPayload,
): TaskSection[] {
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
      tasks: [...getAllFacilities(payload, ['NEW', 'LIVE'])],
    },
    {
      title: 'Excluded facilities',
      tasks: [...getAllFacilities(payload, ['EXCLUDED'])],
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
          status: TaskItemStatus.CANNOT_START_YET,
          link: '',
          linkText: 'Overall decision',
        },
      ],
    },
  ].filter((item) => item.tasks.length > 0);
}

function getAllFacilities(payload: UNAVariationReviewRequestTaskPayload, statuses: Facility['status'][]): TaskItem[] {
  return (
    payload?.underlyingAgreement?.facilities
      ?.filter((facility) => statuses.includes(facility.status))
      ?.map((facility) => ({
        status: payload?.reviewSectionsCompleted?.[facility.facilityId] ?? TaskItemStatus.UNDECIDED,
        link: `${routePrefix}/facility/${facility.facilityId}/summary`,
        linkText: `${facility.facilityDetails.name} (${facility.facilityId})`,
      })) ?? []
  );
}
