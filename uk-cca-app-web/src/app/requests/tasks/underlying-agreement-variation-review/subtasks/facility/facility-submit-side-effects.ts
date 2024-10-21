import { inject } from '@angular/core';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { FACILITIES_SUBTASK, underlyingAgreementQuery } from '@requests/common';

import { submitReviewFacilitySideEffect } from '../../submit-side-effect';

export class FacilitySubmitSideEffects extends SideEffect {
  override subtask = FACILITIES_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];
  override store = inject(RequestTaskStore);
  step: string;

  apply = submitReviewFacilitySideEffect(this.store.select(underlyingAgreementQuery.selectCurrentFacilityId));
}
