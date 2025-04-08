import { Observable, of } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import { PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';
import { produce } from 'immer';

import {
  UnderlyingAgreementActivationDetails,
  UnderlyingAgreementVariationActivationRequestTaskPayload,
} from 'cca-api';

export class ProvideEvidencePayloadMutator extends PayloadMutator {
  override subtask = PROVIDE_EVIDENCE_SUBTASK;

  apply(
    currentPayload: UnderlyingAgreementVariationActivationRequestTaskPayload,
    step,
    userInput: UnderlyingAgreementActivationDetails,
  ): Observable<UnderlyingAgreementVariationActivationRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.underlyingAgreementActivationDetails = userInput;
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.IN_PROGRESS;
      }),
    );
  }
}
