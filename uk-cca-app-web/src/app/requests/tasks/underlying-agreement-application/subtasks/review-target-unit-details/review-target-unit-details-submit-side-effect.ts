import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { REVIEW_TARGET_UNIT_DETAILS_SUBTASK, TaskItemStatus, UNAApplicationRequestTaskPayload } from '@requests/common';
import { produce } from 'immer';

export class ReviewTargetUnitDetailsSubmitSideEffect extends SideEffect {
  override step = undefined;
  override subtask = REVIEW_TARGET_UNIT_DETAILS_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];

  override apply(currentPayload: UNAApplicationRequestTaskPayload): Observable<UNAApplicationRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
