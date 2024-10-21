import { Observable } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import { REVIEW_TARGET_UNIT_DETAILS_SUBTASK, targetUnitDetailsReviewNextStepPath } from '@requests/common';

export class ReviewTargetUnitDetailsStepFlowManager extends WizardFlowManager {
  subtask = REVIEW_TARGET_UNIT_DETAILS_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    return targetUnitDetailsReviewNextStepPath(currentStep);
  }
}
