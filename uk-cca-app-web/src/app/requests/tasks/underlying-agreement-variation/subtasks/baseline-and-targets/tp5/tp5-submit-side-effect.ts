import { Observable, of } from 'rxjs';

import { SideEffect } from '@netz/common/forms';
import { BaselineAndTargetPeriodsSubtasks, TaskItemStatus, UNAVariationRequestTaskPayload } from '@requests/common';
import { produce } from 'immer';

export class Tp5SubmitSideEffect extends SideEffect {
  override step = undefined;
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  override on = ['SUBMIT_SUBTASK'];

  override apply(currentPayload: UNAVariationRequestTaskPayload): Observable<UNAVariationRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
        payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;

        delete payload.reviewGroupDecisions['TARGET_PERIOD5_DETAILS'];
      }),
    );
  }
}
