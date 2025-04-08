import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK, submitReviewSideEffect } from '@requests/common';

export class AuthorisationAdditionalEvidenceSubmitSideEffect extends SideEffect {
  override step = undefined;
  override subtask = AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];

  override apply = submitReviewSideEffect('AUTHORISATION_AND_ADDITIONAL_EVIDENCE', this.subtask);
}
