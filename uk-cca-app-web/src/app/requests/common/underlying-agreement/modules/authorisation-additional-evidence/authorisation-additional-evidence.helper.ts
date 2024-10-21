import { Observable, of } from 'rxjs';

import { Attachments, transformFilesToAttachments, transformFilesToUUIDsList } from '@shared/utils';
import produce from 'immer';

import { AuthorisationAndAdditionalEvidence } from 'cca-api';

import { TaskItemStatus } from '../../../task-item-status';
import {
  AuthorisationAdditionalEvidenceReviewWizardStep,
  AuthorisationAdditionalEvidenceWizardStep,
  AuthorisationAndAdditionalEvidenceUserInput,
  UNARequestTaskPayload,
} from '../../underlying-agreement.types';

export function authorisationAdditionalEvidenceNextStepPath(currentStep: string): Observable<string> {
  switch (currentStep) {
    case AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE:
      return of('../' + AuthorisationAdditionalEvidenceWizardStep.CHECK_YOUR_ANSWERS);
  }
}

export function authorisationAdditionalEvidenceReviewNextStepPath(currentStep: string): Observable<string> {
  switch (currentStep) {
    case AuthorisationAdditionalEvidenceReviewWizardStep.PROVIDE_EVIDENCE:
      return of(AuthorisationAdditionalEvidenceReviewWizardStep.DECISION);

    case AuthorisationAdditionalEvidenceReviewWizardStep.DECISION:
      return of('../' + AuthorisationAdditionalEvidenceReviewWizardStep.CHECK_YOUR_ANSWERS);

    case AuthorisationAdditionalEvidenceReviewWizardStep.CHECK_YOUR_ANSWERS:
      return of('../' + AuthorisationAdditionalEvidenceReviewWizardStep.SUMMARY);
  }
}

export function applyAuthorisationAdditionalEvidence(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: AuthorisationAndAdditionalEvidenceUserInput,
): Observable<UNARequestTaskPayload> {
  const formData: AuthorisationAndAdditionalEvidence = {
    authorisationAttachmentIds: transformFilesToUUIDsList(userInput.authorisationAttachmentIds) as string[],
    additionalEvidenceAttachmentIds: transformFilesToUUIDsList(userInput.additionalEvidenceAttachmentIds) as string[],
  };

  const attachments: Attachments = {
    ...transformFilesToAttachments(userInput.authorisationAttachmentIds),
    ...transformFilesToAttachments(userInput.additionalEvidenceAttachmentIds),
  };

  return of(
    produce(currentPayload, (payload) => {
      payload.underlyingAgreement[subtask] = formData;
      payload.underlyingAgreementAttachments = { ...payload.underlyingAgreementAttachments, ...attachments };
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;
    }),
  );
}
