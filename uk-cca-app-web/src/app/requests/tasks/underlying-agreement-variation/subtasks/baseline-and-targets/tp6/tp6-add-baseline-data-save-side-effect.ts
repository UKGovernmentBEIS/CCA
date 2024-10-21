import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  applyTp6BaselineDataSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  UNAVariationRequestTaskPayload,
} from '@requests/common';

export class Tp6AddBaselineDataSaveSideEffect extends SideEffect {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS;
  override on: SubtaskOperation[] = ['SAVE_SUBTASK'];
  override store = inject(RequestTaskStore);
  step = BaseLineAndTargetsStep.ADD_BASELINE_DATA;

  apply(currentPayload: UNAVariationRequestTaskPayload): Observable<UNAVariationRequestTaskPayload> {
    return applyTp6BaselineDataSideEffect(currentPayload) as Observable<UNAVariationRequestTaskPayload>;
  }
}
