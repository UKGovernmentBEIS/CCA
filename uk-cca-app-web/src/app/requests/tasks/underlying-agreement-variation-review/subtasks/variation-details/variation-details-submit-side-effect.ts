import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { submitReviewSideEffect, VARIATION_DETAILS_SUBTASK } from '@requests/common';

export class VariationDetailsSubmitSideEffect extends SideEffect {
  override step = undefined;
  override subtask = VARIATION_DETAILS_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];

  override apply = submitReviewSideEffect('VARIATION_DETAILS', this.subtask);
}
