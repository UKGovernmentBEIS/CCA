import { Observable } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import { BaselineAndTargetPeriodsSubtasks, targetPeriod6ReviewNextStepPath } from '@requests/common';

export class Tp6StepFlowManager extends WizardFlowManager {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS;

  nextStepPath(currentStep: string): Observable<string> {
    return targetPeriod6ReviewNextStepPath(currentStep);
  }
}
