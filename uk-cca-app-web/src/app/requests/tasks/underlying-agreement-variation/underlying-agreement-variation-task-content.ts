import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  MANAGE_FACILITIES_SUBTASK,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  staticVariationSections,
  TaskItemStatus,
  transformFacilities,
  UNAVariationRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

const routePrefix = 'underlying-agreement-variation';

export const underlyingAgreementVariationTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);

  return {
    header: 'Apply to vary the underlying agreement',
    sections: getAllUnderlyingAgreementVariationSections(store.state?.requestTaskItem?.requestTask?.payload),
  };
};

export function getAllUnderlyingAgreementVariationSections(payload: UNAVariationRequestTaskPayload): TaskSection[] {
  const facilities = transformFacilities(
    payload?.underlyingAgreement?.facilities,
    ['NEW', 'LIVE'],
    payload?.sectionsCompleted,
    routePrefix,
    'summary',
    TaskItemStatus.NOT_STARTED,
  );

  const excludedFacilities = transformFacilities(
    payload?.underlyingAgreement?.facilities,
    ['EXCLUDED'],
    payload?.sectionsCompleted,
    routePrefix,
    'summary',
    TaskItemStatus.NOT_STARTED,
  );

  return [
    {
      title: 'Variation details',
      tasks: [
        {
          status: payload?.sectionsCompleted[VARIATION_DETAILS_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/variation-details`,
          linkText: 'Describe the changes',
        },
      ],
    },
    {
      title: 'Target unit',
      tasks: [
        {
          status: payload?.sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] ?? TaskItemStatus.COMPLETED,
          link: `${routePrefix}/review-target-unit-details`,
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: payload?.sectionsCompleted?.[MANAGE_FACILITIES_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/manage-facilities`,
          linkText: 'Manage facilities list',
        },
        ...facilities,
      ],
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
            payload?.sectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] ??
            TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/target-period-5`,
          linkText: 'TP5 (2021-2022)',
        },
        {
          status:
            payload?.sectionsCompleted?.[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] ??
            TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/target-period-6`,
          linkText: 'TP6 (2024)',
        },
      ],
    },
    {
      title: 'Authorisation details',
      tasks: [
        {
          status: payload?.sectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] ?? TaskItemStatus.NOT_STARTED,
          link: `${routePrefix}/authorisation-additional-evidence`,
          linkText: 'Authorisation and additional evidence',
        },
      ],
    },
    {
      title: 'Send Application',
      tasks: [
        {
          status: allSectionsCompleted(payload) ? TaskItemStatus.NOT_STARTED : TaskItemStatus.CANNOT_START_YET,
          link: allSectionsCompleted(payload) ? `${routePrefix}/send-application` : '',
          linkText: 'Submit to regulator',
        },
      ],
    },
  ].filter((item) => item.tasks.length > 0);
}

function allSectionsCompleted(payload: UNAVariationRequestTaskPayload): boolean {
  return (
    staticVariationSections.every((section) => payload?.sectionsCompleted?.[section] === TaskItemStatus.COMPLETED) &&
    payload.underlyingAgreement.facilities.length &&
    payload?.underlyingAgreement?.facilities?.every(
      (facility) => payload?.sectionsCompleted?.[facility.facilityId] === TaskItemStatus.COMPLETED,
    )
  );
}
