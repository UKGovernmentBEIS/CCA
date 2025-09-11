import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAdditionalEvidenceWizardStep,
  isAdditionalEvidenceWizardCompleted,
  TaskItemStatus,
  underlyingAgreementQuery,
} from '@requests/common';

export const authorisationAdditionalEvidenceRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionStatus = sectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK];
  const statusPending = sectionStatus === TaskItemStatus.NOT_STARTED || sectionStatus === TaskItemStatus.IN_PROGRESS;
  const payload = store.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)();
  const completed = isAdditionalEvidenceWizardCompleted(payload);

  // If section is completed, go to summary
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  // If section is pending (NOT_STARTED or IN_PROGRESS)
  if (statusPending) {
    // If wizard is completed but section status is still pending, go to check answers
    if (completed) return createUrlTreeFromSnapshot(route, ['check-your-answers']);

    // If wizard is not completed, go to provide evidence step
    return createUrlTreeFromSnapshot(route, [AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE]);
  }

  // Fallback for any other cases
  return false;
};
