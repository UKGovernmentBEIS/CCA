import { inject } from '@angular/core';

import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  FACILITIES_SUBTASK,
  TaskItemStatus,
  UNAVariationRequestTaskPayload,
  underlyingAgreementQuery,
} from '@requests/common';
import { produce } from 'immer';

export class FacilitySubmitSideEffects extends SideEffect {
  override subtask = FACILITIES_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];
  override store = inject(RequestTaskStore);
  step: string;

  apply(currentPayload: UNAVariationRequestTaskPayload): Observable<UNAVariationRequestTaskPayload> {
    const currentFacilityId = this.store.select(underlyingAgreementQuery.selectCurrentFacilityId)();

    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[currentFacilityId] = TaskItemStatus.COMPLETED;
      }),
    );
  }
}
