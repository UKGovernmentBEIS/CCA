import { Observable } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import { MANAGE_FACILITIES_SUBTASK, manageFacilitiesNextStepPath } from '@requests/common';

export class ManageFacilitiesStepFlowManager extends WizardFlowManager {
  override subtask = MANAGE_FACILITIES_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    return manageFacilitiesNextStepPath(currentStep);
  }
}
