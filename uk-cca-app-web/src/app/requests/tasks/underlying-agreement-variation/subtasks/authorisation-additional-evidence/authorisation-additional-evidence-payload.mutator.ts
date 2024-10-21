import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyAuthorisationAdditionalEvidence,
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAndAdditionalEvidenceUserInput,
  UNAVariationRequestTaskPayload,
} from '@requests/common';

export class AuthorisationAdditionalEvidencePayloadMutator extends PayloadMutator {
  override subtask = AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK;

  apply(
    currentPayload: UNAVariationRequestTaskPayload,
    step,
    userInput: AuthorisationAndAdditionalEvidenceUserInput,
  ): Observable<UNAVariationRequestTaskPayload> {
    return applyAuthorisationAdditionalEvidence(
      currentPayload,
      this.subtask,
      userInput,
    ) as Observable<UNAVariationRequestTaskPayload>;
  }
}
