import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyAuthorisationAdditionalEvidence,
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAndAdditionalEvidenceUserInput,
  UNAApplicationRequestTaskPayload,
} from '@requests/common';

export class AuthorisationAdditionalEvidencePayloadMutator extends PayloadMutator {
  override subtask = AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK;

  apply(
    currentPayload: UNAApplicationRequestTaskPayload,
    step,
    userInput: AuthorisationAndAdditionalEvidenceUserInput,
  ): Observable<UNAApplicationRequestTaskPayload> {
    return applyAuthorisationAdditionalEvidence(currentPayload, this.subtask, userInput);
  }
}
