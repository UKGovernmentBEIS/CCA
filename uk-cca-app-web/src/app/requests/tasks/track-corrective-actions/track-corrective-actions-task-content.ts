import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestTaskStore } from '@netz/common/store';
import { constructTrackCorrectiveActionsTasks } from '@requests/common';
import { RequestTaskPageContentFactory } from 'projects/common/request-task';

import { AuditTrackCorrectiveActionsRequestTaskPayload } from 'cca-api';

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
