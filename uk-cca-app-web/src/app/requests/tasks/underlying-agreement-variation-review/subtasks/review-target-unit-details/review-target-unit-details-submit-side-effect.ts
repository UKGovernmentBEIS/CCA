import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { REVIEW_TARGET_UNIT_DETAILS_SUBTASK, submitReviewSideEffect } from '@requests/common';

export class ReviewTargetUnitDetailsSubmitSideEffect extends SideEffect {
  override step = undefined;
  override subtask = REVIEW_TARGET_UNIT_DETAILS_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];

  override apply = submitReviewSideEffect('TARGET_UNIT_DETAILS', this.subtask);
}
