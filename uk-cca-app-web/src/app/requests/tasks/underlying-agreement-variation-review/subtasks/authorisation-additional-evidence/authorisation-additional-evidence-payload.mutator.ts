import { map, Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyAuthorisationAdditionalEvidence,
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAndAdditionalEvidenceUserInput,
  TaskItemStatus,
  UNAVariationReviewRequestTaskPayload,
} from '@requests/common';
import produce from 'immer';

export class AuthorisationAdditionalEvidencePayloadMutator extends PayloadMutator {
  override subtask = AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK;

  apply(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    step,
    userInput: AuthorisationAndAdditionalEvidenceUserInput,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    return (
      applyAuthorisationAdditionalEvidence(
        currentPayload,
        this.subtask,
        userInput,
      ) as Observable<UNAVariationReviewRequestTaskPayload>
    ).pipe(
      map((currentPayload) =>
        produce(currentPayload, (payload) => {
          payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
        }),
      ),
    );
  }
}
