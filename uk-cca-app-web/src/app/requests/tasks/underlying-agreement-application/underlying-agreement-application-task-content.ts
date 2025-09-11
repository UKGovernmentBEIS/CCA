import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskPageContentFactory } from '@netz/common/request-task';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  filterEditableTaskLinks,
  nonFacilitySections,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
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
          status: canSubmit(payload) ? TaskItemStatus.NOT_STARTED : TaskItemStatus.CANNOT_START_YET,
          link: canSubmit(payload) ? `${routePrefix}/send-application` : '',
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
          status: manageFacilitiesStatus(payload?.sectionsCompleted),
          link: `${routePrefix}/manage-facilities`,
          linkText: 'Manage facilities',
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

function allSectionsCompleted(payload: UNAApplicationRequestTaskPayload): boolean {
  return Object.values(payload?.sectionsCompleted).every((section) => section === TaskItemStatus.COMPLETED);
}

function manageFacilitiesStatus(sectionsCompleted: Record<string, string>): TaskItemStatus {
  const facilitySections = Object.keys(sectionsCompleted).filter((section) => !nonFacilitySections.includes(section));
  if (facilitySections.length === 0) return TaskItemStatus.NOT_STARTED;

  return facilitySections.every((section) => sectionsCompleted?.[section] === TaskItemStatus.COMPLETED)
    ? TaskItemStatus.COMPLETED
    : TaskItemStatus.IN_PROGRESS;
}

function canSubmit(payload: UNAApplicationRequestTaskPayload) {
  return allSectionsCompleted(payload) && payload?.underlyingAgreement?.facilities?.length > 0;
}
