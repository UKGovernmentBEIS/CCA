import { Observable } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import { VARIATION_DETAILS_SUBTASK, variationDetailsNextStepPath } from '@requests/common';

export class VariationDetailsStepFlowManager extends WizardFlowManager {
  subtask = VARIATION_DETAILS_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    return variationDetailsNextStepPath(currentStep);
  }
}
