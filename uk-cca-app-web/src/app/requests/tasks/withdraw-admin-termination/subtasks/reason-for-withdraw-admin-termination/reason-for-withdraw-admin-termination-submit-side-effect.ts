import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { TaskItemStatus } from '@requests/common';
import produce from 'immer';

import { AdminTerminationWithdrawRequestTaskPayload } from 'cca-api';

import { REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK } from '../../withdraw-admin-termination.types';

export class ReasonForWithdrawAdminTerminationSubmitSideEffect extends SideEffect {
  override subtask: string = REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];
  step: string;

  apply(
    currentPayload: AdminTerminationWithdrawRequestTaskPayload,
  ): Observable<AdminTerminationWithdrawRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
