import { Observable, of } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';

import {
  REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK,
  ReasonForWithdrawAdminTerminationWizardStep,
} from '../../withdraw-admin-termination.types';

export class ReasonForWithdrawAdminTerminationStepFlowManager extends WizardFlowManager {
  override subtask = REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    switch (currentStep) {
      case ReasonForWithdrawAdminTerminationWizardStep.REASON_DETAILS:
        return of(`../${ReasonForWithdrawAdminTerminationWizardStep.CHECK_YOUR_ANSWERS}`);
    }
  }
}
