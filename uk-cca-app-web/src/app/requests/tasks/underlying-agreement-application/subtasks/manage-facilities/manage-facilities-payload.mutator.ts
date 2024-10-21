import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyAddFacility,
  applyDeleteFacility,
  applyEditFacility,
  FacilityItemViewModel,
  MANAGE_FACILITIES_SUBTASK,
  ManageFacilitiesWizardStep,
  UNAApplicationRequestTaskPayload,
} from '@requests/common';

export class ManageFacilitiesPayloadMutator extends PayloadMutator {
  override subtask = MANAGE_FACILITIES_SUBTASK;

  /**
   * @param currentPayload
   * @param userInput The form value of each step
   */
  apply(
    currentPayload: UNAApplicationRequestTaskPayload,
    step: ManageFacilitiesWizardStep,
    userInput: FacilityItemViewModel,
  ): Observable<UNAApplicationRequestTaskPayload> {
    switch (step) {
      case ManageFacilitiesWizardStep.DELETE_FACILITY:
        return applyDeleteFacility(currentPayload, MANAGE_FACILITIES_SUBTASK, userInput);
      case ManageFacilitiesWizardStep.EDIT_FACILITY:
        return applyEditFacility(currentPayload, MANAGE_FACILITIES_SUBTASK, userInput);
      case ManageFacilitiesWizardStep.ADD_FACILITY:
        return applyAddFacility(currentPayload, MANAGE_FACILITIES_SUBTASK, userInput);
    }
  }
}
