import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK, TaskItemStatus, underlyingAgreementQuery } from '@requests/common';

export const authorisationAdditionalEvidenceRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK];
  const statusPending = sectionStatus === TaskItemStatus.NOT_STARTED || sectionStatus === TaskItemStatus.IN_PROGRESS;

  if (statusPending) return createUrlTreeFromSnapshot(route, ['check-your-answers']);

  return createUrlTreeFromSnapshot(route, ['summary']);
};
