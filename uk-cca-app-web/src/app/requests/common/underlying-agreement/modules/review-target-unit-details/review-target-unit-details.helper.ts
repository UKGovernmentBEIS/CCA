import { Observable, of } from 'rxjs';

import { produce } from 'immer';

import { UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { TaskItemStatus } from '../../../task-item-status';
import {
  ReviewTargetUnitDetailsReviewWizardStep,
  ReviewTargetUnitDetailsWizardStep,
  UNARequestTaskPayload,
} from '../../underlying-agreement.types';

export function targetUnitDetailsNextStepPath(currentStep: string): Observable<string> {
  switch (currentStep) {
    case ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS:
    case ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON:
    case ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS:
      return of('../' + ReviewTargetUnitDetailsWizardStep.SUMMARY);
  }
}

export function targetUnitDetailsReviewNextStepPath(currentStep: string): Observable<string> {
  switch (currentStep) {
    case ReviewTargetUnitDetailsReviewWizardStep.OPERATOR_ADDRESS:
    case ReviewTargetUnitDetailsReviewWizardStep.RESPONSIBLE_PERSON:
    case ReviewTargetUnitDetailsReviewWizardStep.TARGET_UNIT_DETAILS:
      return of(ReviewTargetUnitDetailsReviewWizardStep.DECISION);

    case ReviewTargetUnitDetailsReviewWizardStep.CHECK_YOUR_ANSWERS:
      return of('../' + ReviewTargetUnitDetailsReviewWizardStep.SUMMARY);

    case ReviewTargetUnitDetailsReviewWizardStep.DECISION:
      return of('../' + ReviewTargetUnitDetailsReviewWizardStep.CHECK_YOUR_ANSWERS);
  }
}

export function applyTargetUnitDetails(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: UnderlyingAgreementTargetUnitDetails,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.underlyingAgreement[subtask] = { ...payload.underlyingAgreement[subtask], ...userInput };
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;
    }),
  );
}
