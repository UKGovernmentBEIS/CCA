import { Observable, of } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';

import {
  REASON_FOR_ADMIN_TERMINATION_SUBTASK,
  ReasonForAdminTerminationWizardStep,
} from '../../admin-termination.types';

export class ReasonForAdminTerminationStepFlowManager extends WizardFlowManager {
  override subtask = REASON_FOR_ADMIN_TERMINATION_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    switch (currentStep) {
      case ReasonForAdminTerminationWizardStep.REASON_DETAILS:
        return of('../check-your-answers');
    }
  }
}
