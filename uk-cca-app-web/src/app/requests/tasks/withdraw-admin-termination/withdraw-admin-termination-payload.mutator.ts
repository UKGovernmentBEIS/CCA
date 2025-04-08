import { Observable, of } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import { TaskItemStatus } from '@requests/common';
import { transformFilesToUUIDsList } from '@shared/utils';
import { produce } from 'immer';

import { AdminTerminationWithdrawReasonDetails, AdminTerminationWithdrawRequestTaskPayload } from 'cca-api';

import {
  AdminTerminationWithdrawReasonDetailsUserInput,
  REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK,
} from './withdraw-admin-termination.types';

export class WithdrawAdminTerminationSubmitPayloadMutator extends PayloadMutator {
  override subtask = REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK;

  apply(
    currentPayload: AdminTerminationWithdrawRequestTaskPayload,
    step,
    userInput: AdminTerminationWithdrawReasonDetailsUserInput,
  ): Observable<AdminTerminationWithdrawRequestTaskPayload> {
    const formData: AdminTerminationWithdrawReasonDetails = {
      ...userInput,
      relevantFiles: transformFilesToUUIDsList(userInput.relevantFiles) as string[],
    };

    return of(
      produce(currentPayload, (payload) => {
        payload[this.subtask] = formData;
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.IN_PROGRESS;
      }),
    );
  }
}
