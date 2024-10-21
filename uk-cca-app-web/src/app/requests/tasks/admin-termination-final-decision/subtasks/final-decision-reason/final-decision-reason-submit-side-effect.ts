import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { TaskItemStatus } from '@requests/common';
import produce from 'immer';

import { AdminTerminationFinalDecisionRequestTaskPayload } from 'cca-api';

import { ADMIN_TERMINATION_FINAL_DECISION_SUBTASK } from '../../admin-termination-final-decision.helper';

export class FinalDecisionReasonSubmitSideEffect extends SideEffect {
  override subtask = ADMIN_TERMINATION_FINAL_DECISION_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];
  step: string;

  apply(
    currentPayload: AdminTerminationFinalDecisionRequestTaskPayload,
  ): Observable<AdminTerminationFinalDecisionRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
