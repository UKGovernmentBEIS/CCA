import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementVariationActivationRequestTaskPayload } from 'cca-api';

export class ProvideEvidenceSideEffect extends SideEffect {
  override step = undefined;
  override subtask = PROVIDE_EVIDENCE_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];

  override apply(
    currentPayload: UnderlyingAgreementVariationActivationRequestTaskPayload,
  ): Observable<UnderlyingAgreementVariationActivationRequestTaskPayload> {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.subtask] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
