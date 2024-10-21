import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { MANAGE_FACILITIES_SUBTASK, TaskItemStatus, UNAApplicationRequestTaskPayload } from '@requests/common';
import { produce } from 'immer';

export class ManageFacilitiesSubmitSideEffects extends SideEffect {
  override subtask = MANAGE_FACILITIES_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];
  step: string;

  apply(currentPayload: UNAApplicationRequestTaskPayload): Observable<UNAApplicationRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
