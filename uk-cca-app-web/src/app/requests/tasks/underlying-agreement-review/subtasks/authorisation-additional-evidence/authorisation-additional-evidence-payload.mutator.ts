import { map, Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyAuthorisationAdditionalEvidence,
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAndAdditionalEvidenceUserInput,
  TaskItemStatus,
  UNAReviewRequestTaskPayload,
} from '@requests/common';
import produce from 'immer';

export class AuthorisationAdditionalEvidencePayloadMutator extends PayloadMutator {
  override subtask = AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK;

  apply(
    currentPayload: UNAReviewRequestTaskPayload,
    step,
    userInput: AuthorisationAndAdditionalEvidenceUserInput,
  ): Observable<UNAReviewRequestTaskPayload> {
    return (
      applyAuthorisationAdditionalEvidence(
        currentPayload,
        this.subtask,
        userInput,
      ) as Observable<UNAReviewRequestTaskPayload>
    ).pipe(
      map((currentPayload) =>
        produce(currentPayload, (payload) => {
          payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
        }),
      ),
    );
  }
}
