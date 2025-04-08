import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import { applyFacility, FACILITIES_SUBTASK, UNAApplicationRequestTaskPayload } from '@requests/common';

import { Facility } from 'cca-api';

export class FacilityPayloadMutator extends PayloadMutator {
  override subtask = FACILITIES_SUBTASK;

  /**
   * @param currentPayload
   * @param userInput The form value of each step
   */
  apply(
    currentPayload: UNAApplicationRequestTaskPayload,
    step,
    facility: Facility,
  ): Observable<UNAApplicationRequestTaskPayload> {
    return applyFacility(currentPayload, facility);
  }
}
