import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { PROVIDE_EVIDENCE_SUBTASK, ProvideEvidenceWizardStep, TaskItemStatus } from '@requests/common';

import { underlyingAgreementActivationQuery } from '../../+state/una-activation.selectors';
import { isWizardCompleted } from './provide-evidence.wizard';

export const CanActivateProvideEvidence: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const change = route.queryParamMap.get('change') === 'true';

  const details = requestTaskStore.select(
    underlyingAgreementActivationQuery.selectUnderlyingAgreementActivationDetails,
  )();
  const sectionCompleted = requestTaskStore.select(underlyingAgreementActivationQuery.selectSectionsCompleted)()[
    PROVIDE_EVIDENCE_SUBTASK
  ];
  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!change && !isWizardCompleted(details)) return true;
  if (change && isWizardCompleted(details)) return true;

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.SUMMARY]);

  if (!change && isWizardCompleted(details)) {
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
    underlyingAgreementActivationQuery.selectUnderlyingAgreementActivationDetails,
  )();
  const sectionCompleted = requestTaskStore.select(underlyingAgreementActivationQuery.selectSectionsCompleted)()[
    PROVIDE_EVIDENCE_SUBTASK
  ];
  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.SUMMARY]);

  if (!isWizardCompleted(details)) {
    return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.DETAILS]);
  }

  if (sectionCompleted === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.SUMMARY]);
  }

  return true;
};

export const CanActivateProvideEvidenceSummary: CanActivateFn = (route) => {
  const requestTaskStore = inject(RequestTaskStore);

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  const details = requestTaskStore.select(
    underlyingAgreementActivationQuery.selectUnderlyingAgreementActivationDetails,
  )();
  const sectionCompleted = requestTaskStore.select(underlyingAgreementActivationQuery.selectSectionsCompleted)()[
    PROVIDE_EVIDENCE_SUBTASK
  ];

  if (!isEditable) return true;

  if (!isWizardCompleted(details)) {
    return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.DETAILS]);
  }

  if (sectionCompleted === TaskItemStatus.IN_PROGRESS) {
    return createUrlTreeFromSnapshot(route, ['../', ProvideEvidenceWizardStep.CHECK_ANSWERS]);
  }

  return true;
};
