import { map, Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import { applyFacility, FACILITIES_SUBTASK, TaskItemStatus, UNAReviewRequestTaskPayload } from '@requests/common';
import { produce } from 'immer';

import { Facility } from 'cca-api';

export class FacilityPayloadMutator extends PayloadMutator {
  override subtask = FACILITIES_SUBTASK;

  /**
   * @param currentPayload
   * @param userInput The form value of each step
   */
  apply(
    currentPayload: UNAReviewRequestTaskPayload,
    step,
    { facility, attachments }: { facility: Facility; attachments?: { [key: string]: string } },
  ): Observable<UNAReviewRequestTaskPayload> {
    const facilityId = facility.facilityId;

    return (applyFacility(currentPayload, { facility, attachments }) as Observable<UNAReviewRequestTaskPayload>).pipe(
      map((currentPayload) =>
        produce(currentPayload, (payload) => {
          payload.reviewSectionsCompleted[facilityId] = TaskItemStatus.UNDECIDED;
        }),
      ),
    );
  }
}
