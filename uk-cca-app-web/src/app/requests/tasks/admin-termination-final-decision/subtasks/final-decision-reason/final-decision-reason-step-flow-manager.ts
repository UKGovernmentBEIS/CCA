import { Observable, of } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';

import {
  ADMIN_TERMINATION_FINAL_DECISION_SUBTASK,
  AdminTerminationFinalDecisionTerminateAgreementWizardStep,
} from '../../admin-termination-final-decision.helper';

export class FinalDecisionReasonStepFlowManager extends WizardFlowManager {
  override subtask = ADMIN_TERMINATION_FINAL_DECISION_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    switch (currentStep) {
      case AdminTerminationFinalDecisionTerminateAgreementWizardStep.ACTIONS:
        return of(`../${AdminTerminationFinalDecisionTerminateAgreementWizardStep.REASON_DETAILS}`);

      case AdminTerminationFinalDecisionTerminateAgreementWizardStep.REASON_DETAILS:
        return of(`../${'check-your-answers'}`);
    }
  }
}
