import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { MANAGE_FACILITIES_SUBTASK, TaskItemStatus, UNAVariationRequestTaskPayload } from '@requests/common';
import { produce } from 'immer';

export class ManageFacilitiesSubmitSideEffects extends SideEffect {
  override subtask = MANAGE_FACILITIES_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];
  step: string;

  apply(currentPayload: UNAVariationRequestTaskPayload): Observable<UNAVariationRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
