import { inject } from '@angular/core';

import { TaskSection } from '@netz/common/model';
import { RequestActionStore } from '@netz/common/store';
import { constructTrackCorrectiveActionsTasks } from '@requests/common';

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
