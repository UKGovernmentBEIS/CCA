import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { BaselineAndTargetPeriodsSubtasks } from '@requests/common';

import { submitReviewSideEffect } from '../../../submit-side-effect';

export class Tp6SubmitSideEffect extends SideEffect {
  override step = undefined;
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS as string;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];

  override apply = submitReviewSideEffect('TARGET_PERIOD6_DETAILS', this.subtask);
}
