import { Observable, of } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import { PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementActivationDetails } from 'cca-api';

import { UNAActivationRequestTaskPayload } from '../../underlying-agreement-activation.types';

export class ProvideEvidencePayloadMutator extends PayloadMutator {
  override subtask = PROVIDE_EVIDENCE_SUBTASK;

  apply(
    currentPayload: UNAActivationRequestTaskPayload,
    step,
    userInput: { details: UnderlyingAgreementActivationDetails; attachments: Record<string, string> },
  ): Observable<UNAActivationRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.underlyingAgreementActivationDetails = userInput.details;
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.IN_PROGRESS;
      }),
    );
  }
}
