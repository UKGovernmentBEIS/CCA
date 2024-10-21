import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { TaskItemStatus } from '../../../task-item-status';
import { underlyingAgreementQuery } from '../../+state';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAdditionalEvidenceWizardStep,
} from '../../underlying-agreement.types';
import { isAdditionalEvidenceWizardCompleted } from './authorisation-additional-evidence.wizard';

export const CanActivateAuthorisationAndAdditionalEvidence: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const change = route.queryParamMap.get('change') === 'true';
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();

  const authorisationAndAdditionalEvidence = store.select(
    underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence,
  )();

  if (!change && !isAdditionalEvidenceWizardCompleted(authorisationAndAdditionalEvidence)) return true;
  if (change && isAdditionalEvidenceWizardCompleted(authorisationAndAdditionalEvidence)) return true;

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', AuthorisationAdditionalEvidenceWizardStep.SUMMARY]);

  if (!change && isAdditionalEvidenceWizardCompleted(authorisationAndAdditionalEvidence)) {
    if (sectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] === TaskItemStatus.COMPLETED) {
      return createUrlTreeFromSnapshot(route, ['../', AuthorisationAdditionalEvidenceWizardStep.SUMMARY]);
    } else if (sectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] === TaskItemStatus.IN_PROGRESS) {
      return createUrlTreeFromSnapshot(route, ['../', AuthorisationAdditionalEvidenceWizardStep.CHECK_YOUR_ANSWERS]);
    }
    return true;
  }

  return true;
};

export const CanActivateAuthorisationAdditionalEvidenceCheckYourAnswers: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();

  const authorisationAndAdditionalEvidence = store.select(
    underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence,
  )();

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', AuthorisationAdditionalEvidenceWizardStep.SUMMARY]);
  if (!isAdditionalEvidenceWizardCompleted(authorisationAndAdditionalEvidence)) {
    return createUrlTreeFromSnapshot(route, ['../', AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE]);
  }

  if (sectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['../', AuthorisationAdditionalEvidenceWizardStep.SUMMARY]);
  }
  return true;
};
export const CanActivateAuthorisationAdditionalEvidenceSummary: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();

  const authorisationAndAdditionalEvidence = store.select(
    underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence,
  )();

  if (!isEditable) return true;
  if (!isAdditionalEvidenceWizardCompleted(authorisationAndAdditionalEvidence)) {
    return createUrlTreeFromSnapshot(route, ['../', AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE]);
  }
  if (sectionsCompleted[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] === TaskItemStatus.IN_PROGRESS) {
    return createUrlTreeFromSnapshot(route, ['../', AuthorisationAdditionalEvidenceWizardStep.CHECK_YOUR_ANSWERS]);
  }
  return true;
};
