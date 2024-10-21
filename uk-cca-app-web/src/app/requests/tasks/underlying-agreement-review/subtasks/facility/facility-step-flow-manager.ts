import { inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import { FACILITIES_SUBTASK, facilityReviewNextStepPath } from '@requests/common';

export class FacilityStepFlowManager extends WizardFlowManager {
  override subtask = FACILITIES_SUBTASK;
  route = inject(ActivatedRoute);

  nextStepPath(currentStep: string): Observable<string> {
    return facilityReviewNextStepPath(currentStep);
  }
}
