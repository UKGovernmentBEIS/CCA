import { Observable } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import {
  BaselineAndTargetPeriodsSubtasks,
  targetPeriod5NextStepPath,
  underlyingAgreementQuery,
} from '@requests/common';

export class Tp5StepFlowManager extends WizardFlowManager {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;

  nextStepPath(currentStep: string): Observable<string> {
    const targetPeriodExist = this.store.select(underlyingAgreementQuery.selectTargetPeriodExists)();
    return targetPeriod5NextStepPath(currentStep, targetPeriodExist);
  }
}
