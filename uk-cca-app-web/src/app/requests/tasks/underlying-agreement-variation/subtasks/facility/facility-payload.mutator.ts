import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import { applyFacility, FACILITIES_SUBTASK, UNAVariationRequestTaskPayload } from '@requests/common';

import { Facility } from 'cca-api';

export class FacilityPayloadMutator extends PayloadMutator {
  override subtask = FACILITIES_SUBTASK;

  /**
   * @param currentPayload
   * @param userInput The form value of each step
   */
  apply(
    currentPayload: UNAVariationRequestTaskPayload,
    step,
    { facility, attachments }: { facility: Facility; attachments?: { [key: string]: string } },
  ): Observable<UNAVariationRequestTaskPayload> {
    return applyFacility(currentPayload, { facility, attachments }) as Observable<UNAVariationRequestTaskPayload>;
  }
}
