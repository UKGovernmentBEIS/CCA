import { DatePipe } from '@angular/common';

import { TaskItem } from '@netz/common/model';
import { DaysRemainingPipe } from '@netz/common/pipes';

import { AuditCorrectiveActionResponse } from 'cca-api';

import { TaskItemStatus } from '../../task-item-status';
import { TRACK_CORRECTIVE_ACTION_SUBTASK } from './types';

export function constructTrackCorrectiveActionsTasks(
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
