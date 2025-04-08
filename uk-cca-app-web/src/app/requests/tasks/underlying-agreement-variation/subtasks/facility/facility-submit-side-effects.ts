import { inject } from '@angular/core';

import { Observable, of } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  CurrentFacilityId,
  FACILITIES_SUBTASK,
  TaskItemStatus,
  UNAVariationRequestTaskPayload,
} from '@requests/common';
import { produce } from 'immer';

export class FacilitySubmitSideEffects extends SideEffect {
  private readonly currentFacility = inject(CurrentFacilityId);

  override subtask = FACILITIES_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];
  override store = inject(RequestTaskStore);
  step: string;

  apply(currentPayload: UNAVariationRequestTaskPayload): Observable<UNAVariationRequestTaskPayload> {
    if (!this.currentFacility) throw new Error('no currentFacilityId');

    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[this.currentFacility()] = TaskItemStatus.COMPLETED;
        payload.reviewSectionsCompleted[this.currentFacility()] = TaskItemStatus.UNDECIDED;

        delete payload.facilitiesReviewGroupDecisions[this.currentFacility()];
      }),
    );
  }
}
