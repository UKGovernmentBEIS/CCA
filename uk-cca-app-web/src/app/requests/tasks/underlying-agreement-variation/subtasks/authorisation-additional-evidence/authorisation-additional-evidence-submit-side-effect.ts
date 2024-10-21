import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  TaskItemStatus,
  UNAVariationRequestTaskPayload,
} from '@requests/common';
import { produce } from 'immer';

export class AuthorisationAdditionalEvidenceSubmitSideEffect extends SideEffect {
  override step = undefined;
  override subtask = AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];

  override apply(currentPayload: UNAVariationRequestTaskPayload): Observable<UNAVariationRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
