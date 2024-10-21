import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  applyTp5BaselineDataSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  UNAReviewRequestTaskPayload,
} from '@requests/common';

export class Tp5AddBaselineDataSaveSideEffect extends SideEffect {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  override on: SubtaskOperation[] = ['SAVE_SUBTASK'];
  override store = inject(RequestTaskStore);
  step = BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA;

  apply(currentPayload: UNAReviewRequestTaskPayload): Observable<UNAReviewRequestTaskPayload> {
    return applyTp5BaselineDataSideEffect(currentPayload);
  }
}
