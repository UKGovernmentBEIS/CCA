import { Observable, of } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import { PROVIDE_EVIDENCE_SUBTASK, ProvideEvidenceWizardStep } from '@requests/common';

export class ProvideEvidenceStepFlowManager extends WizardFlowManager {
  override subtask = PROVIDE_EVIDENCE_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    switch (currentStep) {
      case ProvideEvidenceWizardStep.DETAILS:
        return of('../' + ProvideEvidenceWizardStep.CHECK_ANSWERS);
    }
  }
}
