import { Observable, of } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import { VARIATION_DETAILS_SUBTASK, VariationDetailsReviewWizardStep } from '@requests/common';

export class VariationDetailsStepFlowManager extends WizardFlowManager {
  subtask = VARIATION_DETAILS_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    switch (currentStep) {
      case VariationDetailsReviewWizardStep.DETAILS:
        return of(VariationDetailsReviewWizardStep.DECISION);
      case VariationDetailsReviewWizardStep.DECISION:
        return of('../' + VariationDetailsReviewWizardStep.CHECK_YOUR_ANSWERS);
      case VariationDetailsReviewWizardStep.CHECK_YOUR_ANSWERS:
        return of('../' + VariationDetailsReviewWizardStep.SUMMARY);
    }
  }
}
