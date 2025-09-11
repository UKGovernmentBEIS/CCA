import { Observable, of } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import { PROVIDE_EVIDENCE_SUBTASK } from '@requests/common';

export class ProvideEvidenceStepFlowManager extends WizardFlowManager {
  override subtask = PROVIDE_EVIDENCE_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    switch (currentStep) {
      case 'details':
        return of('../check-your-answers');
    }
  }
}
