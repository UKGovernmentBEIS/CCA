import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  isStatusFinal,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  staticVariationSections,
  staticVariationSectionsWithoutBaselineAndTargets,
  TaskItemStatus,
  UNAVariationRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

const routePrefix = 'underlying-agreement-variation';

export const underlyingAgreementVariationTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(requestTaskQuery.selectRequestTaskPayload)();

  return {
    header: 'Apply to vary the underlying agreement',
    sections: getAllUnderlyingAgreementVariationSections(payload),
  };
};

export function getAllUnderlyingAgreementVariationSections(payload: UNAVariationRequestTaskPayload): TaskSection[] {
  const sections: TaskSection[] = [
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
          status: payload?.sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] ?? TaskItemStatus.UNCHANGED,
          link: `${routePrefix}/review-target-unit-details`,
          linkText: 'Target unit details',
        },
      ],
    },
    {
      title: 'Facilities',
      tasks: [
        {
          status: manageFacilitiesStatus(payload),
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
    });
  }

  sections.push(
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
  );

  return sections;
}

function allSectionsCompleted(payload: UNAVariationRequestTaskPayload): boolean {
  if (!!payload?.underlyingAgreement?.targetPeriod5Details && !!payload?.underlyingAgreement?.targetPeriod6Details) {
    return staticVariationSections.every((section) => isStatusFinal(payload?.sectionsCompleted?.[section]));
  }

  return staticVariationSectionsWithoutBaselineAndTargets.every((section) =>
    isStatusFinal(payload?.sectionsCompleted?.[section]),
  );
}

function manageFacilitiesStatus(payload: UNAVariationRequestTaskPayload): TaskItemStatus {
  const facilitySections = Object.keys(payload?.sectionsCompleted).filter(
    (section) => !staticVariationSections.includes(section),
  );
  if (facilitySections.length === 0) throw new Error('No facility found.');

  const inProgressExists = facilitySections.some(
    (section) => payload?.sectionsCompleted?.[section] === TaskItemStatus.IN_PROGRESS,
  );
  const validFacilities = payload?.underlyingAgreement?.facilities?.filter((f) => f.status !== 'EXCLUDED');
  if (validFacilities.length === 0 || inProgressExists) return TaskItemStatus.IN_PROGRESS;

  if (facilitySections.every((section) => payload?.sectionsCompleted?.[section] === TaskItemStatus.COMPLETED)) {
    return TaskItemStatus.COMPLETED;
  }

  if (facilitySections.every((section) => payload?.sectionsCompleted?.[section] === TaskItemStatus.UNCHANGED)) {
    return TaskItemStatus.UNCHANGED;
  }

  const completedExists = facilitySections.some(
    (section) => payload?.sectionsCompleted?.[section] === TaskItemStatus.COMPLETED,
  );
  if (completedExists && !inProgressExists) return TaskItemStatus.COMPLETED;
}

function canSubmit(payload: UNAVariationRequestTaskPayload): boolean {
  return (
    allSectionsCompleted(payload) &&
    (manageFacilitiesStatus(payload) === TaskItemStatus.COMPLETED ||
      manageFacilitiesStatus(payload) === TaskItemStatus.UNCHANGED)
  );
}
