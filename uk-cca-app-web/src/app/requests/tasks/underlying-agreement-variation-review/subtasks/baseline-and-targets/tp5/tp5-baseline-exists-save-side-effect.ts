import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  applyTp5ExistSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  UNAVariationReviewRequestTaskPayload,
} from '@requests/common';

export class Tp5BaselineExistsSaveSideEffect extends SideEffect {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  override on: SubtaskOperation[] = ['SAVE_SUBTASK'];
  override store = inject(RequestTaskStore);
  step = BaseLineAndTargetsStep.BASELINE_EXISTS;

  apply(currentPayload: UNAVariationReviewRequestTaskPayload): Observable<UNAVariationReviewRequestTaskPayload> {
    return applyTp5ExistSideEffect(currentPayload, this.subtask) as Observable<UNAVariationReviewRequestTaskPayload>;
  }
}
