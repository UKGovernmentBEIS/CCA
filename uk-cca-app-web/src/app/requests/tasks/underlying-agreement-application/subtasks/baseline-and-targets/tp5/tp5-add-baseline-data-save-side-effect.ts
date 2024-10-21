import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  applyTp5BaselineDataSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  UNAApplicationRequestTaskPayload,
} from '@requests/common';

export class Tp5AddBaselineDataSaveSideEffect extends SideEffect {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  override on: SubtaskOperation[] = ['SAVE_SUBTASK'];
  override store = inject(RequestTaskStore);
  step = BaseLineAndTargetsStep.ADD_BASELINE_DATA;

  apply(currentPayload: UNAApplicationRequestTaskPayload): Observable<UNAApplicationRequestTaskPayload> {
    return applyTp5BaselineDataSideEffect(currentPayload);
  }
}
