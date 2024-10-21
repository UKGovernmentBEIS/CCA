import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  applyTp6BaselineDataSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  UNAVariationReviewRequestTaskPayload,
} from '@requests/common';

export class Tp6AddBaselineDataSaveSideEffect extends SideEffect {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS;
  override on: SubtaskOperation[] = ['SAVE_SUBTASK'];
  override store = inject(RequestTaskStore);
  step = BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA;

  apply(currentPayload: UNAVariationReviewRequestTaskPayload): Observable<UNAVariationReviewRequestTaskPayload> {
    return applyTp6BaselineDataSideEffect(currentPayload) as Observable<UNAVariationReviewRequestTaskPayload>;
  }
}
