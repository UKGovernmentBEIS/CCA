import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';

import { underlyingAgreementActivationQuery } from '../../+state/una-activation.selectors';
import { isWizardCompleted } from './provide-evidence.wizard';

export const CanActivateProvideEvidence: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', 'summary']);

  const details = requestTaskStore.select(
    underlyingAgreementActivationQuery.selectUnderlyingAgreementActivationDetails,
  )();

  const sectionCompleted = requestTaskStore.select(underlyingAgreementActivationQuery.selectSectionsCompleted)()[
    PROVIDE_EVIDENCE_SUBTASK
  ];

  const change = route.queryParamMap.get('change') === 'true';
  if (!change && !isWizardCompleted(details)) return true;
  if (change && isWizardCompleted(details)) return true;

  if (!change && isWizardCompleted(details)) {
    if (sectionCompleted === TaskItemStatus.COMPLETED) {
      return createUrlTreeFromSnapshot(route, ['../', 'summary']);
    } else if (sectionCompleted === TaskItemStatus.IN_PROGRESS) {
      return createUrlTreeFromSnapshot(route, ['../', 'check-your-answers']);
    }
  }

  return true;
};

export const CanActivateProvideEvidenceCheckYourAnswers: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', 'summary']);

  const details = requestTaskStore.select(
    underlyingAgreementActivationQuery.selectUnderlyingAgreementActivationDetails,
  )();

  if (!isWizardCompleted(details)) return createUrlTreeFromSnapshot(route, ['../', 'details']);

  const sectionCompleted = requestTaskStore.select(underlyingAgreementActivationQuery.selectSectionsCompleted)()[
    PROVIDE_EVIDENCE_SUBTASK
  ];

  if (sectionCompleted === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['../', 'summary']);

  return true;
};

export const CanActivateProvideEvidenceSummary: CanActivateFn = (route) => {
  const requestTaskStore = inject(RequestTaskStore);

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return true;

  const details = requestTaskStore.select(
    underlyingAgreementActivationQuery.selectUnderlyingAgreementActivationDetails,
  )();

  if (!isWizardCompleted(details)) return createUrlTreeFromSnapshot(route, ['../', 'details']);

  const sectionCompleted = requestTaskStore.select(underlyingAgreementActivationQuery.selectSectionsCompleted)()[
    PROVIDE_EVIDENCE_SUBTASK
  ];

  if (sectionCompleted === TaskItemStatus.IN_PROGRESS)
    return createUrlTreeFromSnapshot(route, ['../', 'check-your-answers']);

  return true;
};
