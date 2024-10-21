import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  applyTp5TargetCompositionSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  UNAReviewRequestTaskPayload,
} from '@requests/common';

export class Tp5TargetCompositionSaveSideEffect extends SideEffect {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  override on: SubtaskOperation[] = ['SAVE_SUBTASK'];
  override store = inject(RequestTaskStore);
  step = BaseLineAndTargetsReviewStep.TARGET_COMPOSITION;

  apply(currentPayload: UNAReviewRequestTaskPayload): Observable<UNAReviewRequestTaskPayload> {
    return applyTp5TargetCompositionSideEffect(currentPayload);
  }
}
