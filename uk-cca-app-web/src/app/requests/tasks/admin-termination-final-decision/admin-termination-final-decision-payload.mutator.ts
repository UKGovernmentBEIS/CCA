import { Observable, of } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import { TaskItemStatus } from '@requests/common';
import { fileUtils } from '@shared/utils';
import { produce } from 'immer';

import { AdminTerminationFinalDecisionReasonDetails, AdminTerminationFinalDecisionRequestTaskPayload } from 'cca-api';

import {
  ADMIN_TERMINATION_FINAL_DECISION_SUBTASK,
  AdminTerminationFinalDecisionReasonDetailsUserInput,
  AdminTerminationFinalDecisionTerminateAgreementWizardStep,
} from './admin-termination-final-decision.helper';

export class AdminTerminationFinalDecisionPayloadMutator extends PayloadMutator {
  override subtask = ADMIN_TERMINATION_FINAL_DECISION_SUBTASK;

  apply(
    currentPayload: AdminTerminationFinalDecisionRequestTaskPayload,
    step,
    userInput: AdminTerminationFinalDecisionReasonDetailsUserInput,
  ): Observable<AdminTerminationFinalDecisionRequestTaskPayload> {
    if (step === AdminTerminationFinalDecisionTerminateAgreementWizardStep.ACTIONS) {
      return of(
        produce(currentPayload, (payload) => {
          payload[this.subtask].finalDecisionType = userInput.finalDecisionType;
        }),
      );
    } else {
      const formData: AdminTerminationFinalDecisionReasonDetails = {
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
}
