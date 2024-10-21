import { Observable } from 'rxjs';

import { WizardFlowManager } from '@netz/common/forms';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  authorisationAdditionalEvidenceReviewNextStepPath,
} from '@requests/common';

export class AuthorisationAdditionalEvidenceStepFlowManager extends WizardFlowManager {
  subtask = AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK;

  nextStepPath(currentStep: string): Observable<string> {
    return authorisationAdditionalEvidenceReviewNextStepPath(currentStep);
  }
}
