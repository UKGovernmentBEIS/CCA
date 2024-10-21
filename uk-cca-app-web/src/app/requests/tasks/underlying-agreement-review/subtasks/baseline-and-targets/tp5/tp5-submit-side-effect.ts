import { SideEffect } from '@netz/common/forms';
import { BaselineAndTargetPeriodsSubtasks } from '@requests/common';

import { submitReviewSideEffect } from '../../../submit-side-effect';

export class Tp5SubmitSideEffect extends SideEffect {
  override step = undefined;
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  override on = ['SUBMIT_SUBTASK'];

  override apply = submitReviewSideEffect('TARGET_PERIOD5_DETAILS', this.subtask);
}
