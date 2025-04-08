import { inject } from '@angular/core';

import { SideEffect, SubtaskOperation } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { CurrentFacilityId, FACILITIES_SUBTASK } from '@requests/common';
import { submitReviewFacilitySideEffect } from '@requests/common';

export class FacilitySubmitSideEffects extends SideEffect {
  override subtask = FACILITIES_SUBTASK;
  override on: SubtaskOperation[] = ['SUBMIT_SUBTASK'];
  override store = inject(RequestTaskStore);
  private readonly currentFacilityId = inject(CurrentFacilityId);

  step: string;
  apply = submitReviewFacilitySideEffect(this.currentFacilityId);
}
