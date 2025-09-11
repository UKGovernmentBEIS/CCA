import { Observable, of } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import { TaskItemStatus } from '@requests/common';
import { fileUtils } from '@shared/utils';
import { produce } from 'immer';

import { AdminTerminationReasonDetails, AdminTerminationSubmitRequestTaskPayload } from 'cca-api';

import {
  AdminTerminationReasonDetailsUserInput,
  REASON_FOR_ADMIN_TERMINATION_SUBTASK,
} from './admin-termination.types';

export class AdminTerminationSubmitPayloadMutator extends PayloadMutator {
  override subtask = REASON_FOR_ADMIN_TERMINATION_SUBTASK;

  apply(
    currentPayload: AdminTerminationSubmitRequestTaskPayload,
    step,
    userInput: AdminTerminationReasonDetailsUserInput,
  ): Observable<AdminTerminationSubmitRequestTaskPayload> {
    const formData: AdminTerminationReasonDetails = {
      ...userInput,
      relevantFiles: fileUtils.toUUIDs(userInput.relevantFiles),
    };

    return of(
      produce(currentPayload, (payload) => {
        payload[this.subtask] = formData;
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.IN_PROGRESS;
      }),
    );
  }
}
