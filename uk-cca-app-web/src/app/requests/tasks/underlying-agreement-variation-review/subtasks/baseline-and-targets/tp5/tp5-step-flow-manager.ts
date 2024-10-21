import { Observable } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import {
  BaselineAndTargetPeriodsSubtasks,
  targetPeriod5ReviewNextStepPath,
  underlyingAgreementQuery,
} from '@requests/common';

export class Tp5StepFlowManager extends WizardFlowManager {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;

  nextStepPath(currentStep: string): Observable<string> {
    const baselineExists = this.store.select(underlyingAgreementQuery.selectTargetPeriodExists)();
    return targetPeriod5ReviewNextStepPath(currentStep, baselineExists);
  }
}
