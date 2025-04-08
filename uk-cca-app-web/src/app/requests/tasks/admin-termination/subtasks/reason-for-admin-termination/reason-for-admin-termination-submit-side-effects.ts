import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { TaskItemStatus } from '@requests/common';
import { produce } from 'immer';

import { AdminTerminationSubmitRequestTaskPayload } from 'cca-api';

import { REASON_FOR_ADMIN_TERMINATION_SUBTASK } from '../../admin-termination.types';

export class ReasonForAdminTerminationSubmitSideEffect extends SideEffect {
  override subtask = REASON_FOR_ADMIN_TERMINATION_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];
  step: string;

  apply(
    currentPayload: AdminTerminationSubmitRequestTaskPayload,
  ): Observable<AdminTerminationSubmitRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
