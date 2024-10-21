import { Observable } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import { FACILITIES_SUBTASK, facilityNextStepPath } from '@requests/common';

export class FacilityStepFlowManager extends WizardFlowManager {
  override subtask = FACILITIES_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    return facilityNextStepPath(currentStep);
  }
}
