import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus, TRACK_CORRECTIVE_ACTION_SUBTASK } from '@requests/common';

import { AuditCorrectiveActionResponse } from 'cca-api';

import { trackCorrectiveActionsQuery } from '../../track-corrective-actions.selectors';

export const trackActionsRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const actionId = route.params.actionId;

  const sectionsCompleted = store.select(trackCorrectiveActionsQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[`${TRACK_CORRECTIVE_ACTION_SUBTASK}${actionId}`];

  const correctiveActionResponses = store.select(trackCorrectiveActionsQuery.selectAuditTrackCorrectiveActions)()
    ?.correctiveActionResponses;

  if (!isWizardCompleted(correctiveActionResponses[actionId]))
    return createUrlTreeFromSnapshot(route, ['is-carried-out']);

  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};

function isWizardCompleted(correctiveActionResponse: AuditCorrectiveActionResponse): boolean {
  return (
    correctiveActionResponse?.isActionCarriedOut === false ||
    (correctiveActionResponse?.isActionCarriedOut &&
      !!correctiveActionResponse?.actionCarriedOutDate &&
      !!correctiveActionResponse?.comments)
  );
}
