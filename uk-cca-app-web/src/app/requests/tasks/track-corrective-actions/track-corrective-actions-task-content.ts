import { DatePipe } from '@angular/common';
import { inject } from '@angular/core';

import { TaskItem, TaskSection } from '@netz/common/model';
import { DaysRemainingPipe } from '@netz/common/pipes';
import { RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus, TRACK_CORRECTIVE_ACTION_SUBTASK } from '@requests/common';
import { RequestTaskPageContentFactory } from 'projects/common/request-task';

import { AuditCorrectiveActionResponse, AuditTrackCorrectiveActionsRequestTaskPayload } from 'cca-api';

import { CompleteTaskButtonComponent } from './complete-task/complete-task-button.component';
import { trackCorrectiveActionsQuery } from './track-corrective-actions.selectors';

const routePrefix = 'track-corrective-actions';

export const trackCorrectiveActionsTaskContent: RequestTaskPageContentFactory = () => {
  const payload = inject(RequestTaskStore).select(trackCorrectiveActionsQuery.selectPayload)();

  return {
    header: 'Track corrective actions',
    preContentComponent: CompleteTaskButtonComponent,
    sections: trackCorrectiveActionsSections(payload),
  };
};

function trackCorrectiveActionsSections(payload: AuditTrackCorrectiveActionsRequestTaskPayload): TaskSection[] {
  return [
    {
      title: 'Review corrective actions',
      tasks: constructTrackCorrectiveActionsTasks(
        payload.auditTrackCorrectiveActions.correctiveActionResponses,
        routePrefix,
        payload?.sectionsCompleted,
      ),
    },
  ];
}

function constructTrackCorrectiveActionsTasks(
  correctiveActionResponses: Record<string, AuditCorrectiveActionResponse>,
  routePrefix: string,
  sectionsCompleted?: Record<string, string>,
): TaskItem[] {
  const datePipe = new DatePipe('en-GB');
  const daysRemainingPipe = new DaysRemainingPipe();

  const entries: TaskItem[] = Object.values(correctiveActionResponses).map((r) => {
    const daysRemaining = daysRemainingPipe.calcRemainingDays(r.deadline);
    const days = daysRemaining >= 0 ? daysRemaining.toString() : 'Overdue';
    const finalDate = daysRemaining >= 0 ? `(until ${datePipe.transform(r.deadline, 'longDate')})` : '';
    const hint = `<p>Days remaining: ${days} ${finalDate}</p><p>${r.details}</p>`;
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
