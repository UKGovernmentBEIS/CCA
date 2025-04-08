import { Observable, switchMap } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyAddFacility,
  applyDeleteFacility,
  applyEditFacility,
  applyEditFacilityVariationReviewStatusChanges,
  applyExcludeFacility,
  applyExcludeFacilityVariationReviewStatusChanges,
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
   * In the cases of `EDIT_FACILITY`, `EXCLUDE_FACILITY` and `UNDO_FACILITY` we need
   * to apply extra mutations to the Variation Review sections, to satisfy the conditions described
   * in CCA-1053.
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
        return applyEditFacility(currentPayload, MANAGE_FACILITIES_SUBTASK, userInput).pipe(
          switchMap((mutatedPayload) => applyEditFacilityVariationReviewStatusChanges(mutatedPayload, userInput)),
        ) as Observable<UNAVariationRequestTaskPayload>;

      case ManageFacilitiesWizardStep.ADD_FACILITY:
        return applyAddFacility(
          currentPayload,
          MANAGE_FACILITIES_SUBTASK,
          userInput,
        ) as Observable<UNAVariationRequestTaskPayload>;

      case ManageFacilitiesWizardStep.EXCLUDE_FACILITY:
        return applyExcludeFacility(currentPayload, MANAGE_FACILITIES_SUBTASK, userInput).pipe(
          switchMap((mutatedPayload) => applyExcludeFacilityVariationReviewStatusChanges(mutatedPayload, userInput)),
        ) as Observable<UNAVariationRequestTaskPayload>;

      // If the facility is editted and then excluded, we don't have the info to handle the review status change,
      // and as such it remains as undecided from the exclude case.
      case ManageFacilitiesWizardStep.UNDO_FACILITY:
        return applyUndoFacility(
          currentPayload,
          MANAGE_FACILITIES_SUBTASK,
          userInput,
        ) as Observable<UNAVariationRequestTaskPayload>;
    }
  }
}
