import { inject } from '@angular/core';

import { TaskItem, TaskSection } from '@netz/common/model';
import { RequestActionStore } from '@netz/common/store';
import { TaskItemStatus, TRACK_CORRECTIVE_ACTION_SUBTASK } from '@requests/common';

import { AuditCorrectiveActionResponse } from 'cca-api';

import { trackCorrectiveActionsCompletedQuery } from './track-corrective-actions-completed.selectors';

const routePrefix = 'track-corrective-actions';

export function getTrackCorrectiveActionsSections(): TaskSection[] {
  const payload = inject(RequestActionStore).select(trackCorrectiveActionsCompletedQuery.selectActionPayload)();

  return [
    {
      title: 'Review corrective actions',
      tasks: constructTrackCorrectiveActionsTasks(
        payload.auditTrackCorrectiveActions.correctiveActionResponses,
        routePrefix,
      ),
    },
  ];
}

function constructTrackCorrectiveActionsTasks(
  correctiveActionResponses: Record<string, AuditCorrectiveActionResponse>,
  routePrefix: string,
  sectionsCompleted?: Record<string, string>,
): TaskItem[] {
  const entries: TaskItem[] = Object.values(correctiveActionResponses).map((r) => {
    const hint = `<p>${r.details}</p>`;
    const status = sectionsCompleted
      ? (sectionsCompleted[`${TRACK_CORRECTIVE_ACTION_SUBTASK}${r.title}`] ?? TaskItemStatus.NOT_STARTED)
      : '';

    return {
      link: `${routePrefix}/${r.title}`,
      linkText: `Corrective action ${r.title}`,
      status,
      hint,
    };
  });

  return entries;
}
