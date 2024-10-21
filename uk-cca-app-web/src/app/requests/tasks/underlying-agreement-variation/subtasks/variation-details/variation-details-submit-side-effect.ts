import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { TaskItemStatus, UNAVariationRequestTaskPayload, VARIATION_DETAILS_SUBTASK } from '@requests/common';
import { produce } from 'immer';

export class VariationDetailsSubmitSideEffect extends SideEffect {
  override step = undefined;
  override subtask = VARIATION_DETAILS_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];

  override apply(currentPayload: UNAVariationRequestTaskPayload): Observable<UNAVariationRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
