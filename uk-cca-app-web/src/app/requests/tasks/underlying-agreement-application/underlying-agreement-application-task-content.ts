import { inject } from '@angular/core';

import { TaskItem, TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  filterEditableTaskLinks,
  MANAGE_FACILITIES_SUBTASK,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  staticSections,
  TaskItemStatus,
  UNAApplicationRequestTaskPayload,
} from '@requests/common';

const routePrefix = 'underlying-agreement-application';

export const underlyingAgreementApplicationTaskContent: RequestTaskPageContentFactory = () => {
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  const payload = store.state?.requestTaskItem?.requestTask?.payload;
  const sections = getAllUnderlyingAgreementApplicationSections(payload);

  if (isEditable) {
    sections.push({
      title: 'Send Application',
      tasks: [
        {
          status: allSectionsCompleted(payload) ? TaskItemStatus.NOT_STARTED : TaskItemStatus.CANNOT_START_YET,
          link: allSectionsCompleted(payload) ? `${routePrefix}/send-application` : '',
          linkText: 'Submit to regulator',
        },
      ],
    });
  }

  return {
    header: 'Apply for an underlying agreement',
    sections: filterEditableTaskLinks(sections, isEditable),
  };
};

export function getAllUnderlyingAgreementApplicationSections(payload: UNAApplicationRequestTaskPayload): TaskSection[] {
  return [
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
        ...getAllFacilities(payload),
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
  ];
}

function getAllFacilities(payload: UNAApplicationRequestTaskPayload): TaskItem[] {
  return (
    payload?.underlyingAgreement?.facilities?.map((facility) => ({
      status: payload?.sectionsCompleted?.[facility.facilityId] ?? TaskItemStatus.NOT_STARTED,
      link: `${routePrefix}/facility/${facility.facilityId}/summary`,
      linkText: `${facility.facilityDetails.name} (${facility.facilityId})`,
    })) ?? []
  );
}

function allSectionsCompleted(payload: UNAApplicationRequestTaskPayload): boolean {
  return (
    staticSections.every((section) => payload?.sectionsCompleted?.[section] === TaskItemStatus.COMPLETED) &&
    payload.underlyingAgreement.facilities.length &&
    payload?.underlyingAgreement?.facilities?.every(
      (facility) => payload?.sectionsCompleted?.[facility.facilityId] === TaskItemStatus.COMPLETED,
    )
  );
}
