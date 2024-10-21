import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyAddFacility,
  applyDeleteFacility,
  applyEditFacility,
  applyExcludeFacility,
  applyUndoFacility,
  FacilityItemViewModel,
  MANAGE_FACILITIES_SUBTASK,
  ManageFacilitiesWizardStep,
  UNAVariationRequestTaskPayload,
} from '@requests/common';

export class ManageFacilitiesPayloadMutator extends PayloadMutator {
  override subtask = MANAGE_FACILITIES_SUBTASK;

  /**
   * @param currentPayload
   * @param userInput The form value of each step
   */
  apply(
    currentPayload: UNAVariationRequestTaskPayload,
    step: ManageFacilitiesWizardStep,
    userInput: FacilityItemViewModel,
  ): Observable<UNAVariationRequestTaskPayload> {
    switch (step) {
      case ManageFacilitiesWizardStep.DELETE_FACILITY:
        return applyDeleteFacility(
          currentPayload,
          MANAGE_FACILITIES_SUBTASK,
          userInput,
        ) as Observable<UNAVariationRequestTaskPayload>;

      case ManageFacilitiesWizardStep.EDIT_FACILITY:
        return applyEditFacility(
          currentPayload,
          MANAGE_FACILITIES_SUBTASK,
          userInput,
        ) as Observable<UNAVariationRequestTaskPayload>;

      case ManageFacilitiesWizardStep.ADD_FACILITY:
        return applyAddFacility(
          currentPayload,
          MANAGE_FACILITIES_SUBTASK,
          userInput,
        ) as Observable<UNAVariationRequestTaskPayload>;

      case ManageFacilitiesWizardStep.EXCLUDE_FACILITY:
        return applyExcludeFacility(
          currentPayload,
          MANAGE_FACILITIES_SUBTASK,
          userInput,
        ) as Observable<UNAVariationRequestTaskPayload>;

      case ManageFacilitiesWizardStep.UNDO_FACILITY:
        return applyUndoFacility(
          currentPayload,
          MANAGE_FACILITIES_SUBTASK,
          userInput,
        ) as Observable<UNAVariationRequestTaskPayload>;
    }
  }
}
