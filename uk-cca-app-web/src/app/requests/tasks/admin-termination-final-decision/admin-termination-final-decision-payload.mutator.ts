import { Observable, of } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import { TaskItemStatus } from '@requests/common';
import { transformFilesToAttachments, transformFilesToUUIDsList } from '@shared/utils';
import produce from 'immer';

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
        relevantFiles: transformFilesToUUIDsList(userInput.relevantFiles) as string[],
      };

      const attachments = transformFilesToAttachments(userInput.relevantFiles ?? []);

      return of(
        produce(currentPayload, (payload) => {
          payload[this.subtask] = formData;
          payload.adminTerminationAttachments = { ...payload.adminTerminationAttachments, ...attachments };
          payload.sectionsCompleted[this.subtask] = TaskItemStatus.IN_PROGRESS;
        }),
      );
    }
  }
}
