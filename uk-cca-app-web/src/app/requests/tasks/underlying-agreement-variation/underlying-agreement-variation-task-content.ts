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
          status: manageFacilitiesStatus(payload?.sectionsCompleted),
          link: `${routePrefix}/manage-facilities`,
          linkText: 'Manage facilities',
        },
      ],
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
          status: canSubmit(payload) ? TaskItemStatus.NOT_STARTED : TaskItemStatus.CANNOT_START_YET,
          link: canSubmit(payload) ? `${routePrefix}/send-application` : '',
          linkText: 'Submit to regulator',
        },
      ],
    },
  ].filter((item) => item.tasks.length > 0);
}

function allSectionsCompleted(payload: UNAVariationRequestTaskPayload): boolean {
  return staticVariationSections.every((section) => payload?.sectionsCompleted?.[section] === TaskItemStatus.COMPLETED);
}

function manageFacilitiesStatus(sectionsCompleted: Record<string, string>): TaskItemStatus {
  // TODO: Remove MANAGE_FACILITIES_SUBTASK section when the CCA3 specification is finalized and it's removed from the database sections.
  const facilitySections = Object.keys(sectionsCompleted).filter(
    (section) => ![MANAGE_FACILITIES_SUBTASK, ...staticVariationSections].includes(section),
  );

  if (facilitySections.length === 0) throw new Error('No facility found.');

  return facilitySections.every((section) => sectionsCompleted?.[section] === TaskItemStatus.COMPLETED)
    ? TaskItemStatus.COMPLETED
    : TaskItemStatus.IN_PROGRESS;
}

function canSubmit(payload: UNAVariationRequestTaskPayload) {
  return allSectionsCompleted(payload) && payload?.underlyingAgreement?.facilities?.length > 0;
}
