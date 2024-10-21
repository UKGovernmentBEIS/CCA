import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { BaselineAndTargetPeriodsSubtasks, TaskItemStatus, UNAVariationRequestTaskPayload } from '@requests/common';
import { produce } from 'immer';

export class Tp6SubmitSideEffect extends SideEffect {
  override step = undefined;
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS as string;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];

  override apply(currentPayload: UNAVariationRequestTaskPayload): Observable<UNAVariationRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
