import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PROVIDE_EVIDENCE_SUBTASK, ProvideEvidenceWizardStep, TaskItemStatus } from '@requests/common';

import { underlyingAgreementVariationActivationQuery } from '../../+state/una-variation-activation.selectors';
import { isWizardCompleted } from './provide-evidence.wizard';

export const CanActivateProvideEvidence: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const change = route.queryParamMap.get('change') === 'true';

  const details = requestTaskStore.select(
    underlyingAgreementVariationActivationQuery.selectUnderlyingAgreementActivationDetails,
  )();

  if (!change && !isWizardCompleted(details)) return true;
  if (change && isWizardCompleted(details)) return true;

  if (!change && isWizardCompleted(details)) {
    const sectionCompleted = requestTaskStore.select(
      underlyingAgreementVariationActivationQuery.selectSectionsCompleted,
    )()[PROVIDE_EVIDENCE_SUBTASK];

    if (sectionCompleted === TaskItemStatus.COMPLETED) {
      return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.SUMMARY]);
    } else if (sectionCompleted === TaskItemStatus.IN_PROGRESS) {
      return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.CHECK_ANSWERS]);
    }
  }

  return true;
};

export const CanActivateProvideEvidenceCheckYourAnswers: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const details = requestTaskStore.select(
    underlyingAgreementVariationActivationQuery.selectUnderlyingAgreementActivationDetails,
  )();

  if (!isWizardCompleted(details)) {
    return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.DETAILS]);
  }

  const sectionCompleted = requestTaskStore.select(
    underlyingAgreementVariationActivationQuery.selectSectionsCompleted,
  )()[PROVIDE_EVIDENCE_SUBTASK];

  if (sectionCompleted === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.SUMMARY]);
  }

  return true;
};

export const CanActivateProvideEvidenceSummary: CanActivateFn = (route) => {
  const requestTaskStore = inject(RequestTaskStore);

  const details = requestTaskStore.select(
    underlyingAgreementVariationActivationQuery.selectUnderlyingAgreementActivationDetails,
  )();

  if (!isWizardCompleted(details)) {
    return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.DETAILS]);
  }

  const sectionCompleted = requestTaskStore.select(
    underlyingAgreementVariationActivationQuery.selectSectionsCompleted,
  )()[PROVIDE_EVIDENCE_SUBTASK];

  if (sectionCompleted === TaskItemStatus.IN_PROGRESS) {
    return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.CHECK_ANSWERS]);
  }

  return true;
};
