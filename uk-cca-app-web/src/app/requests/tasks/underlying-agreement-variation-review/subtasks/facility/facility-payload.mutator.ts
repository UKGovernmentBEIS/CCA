import { map, Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyFacility,
  FACILITIES_SUBTASK,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  UNAVariationReviewRequestTaskPayload,
} from '@requests/common';
import { produce } from 'immer';

import { Facility } from 'cca-api';

export class FacilityPayloadMutator extends PayloadMutator {
  override subtask = FACILITIES_SUBTASK;

  /**
   * @param currentPayload
   * @param userInput The form value of each step
   */
  apply(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    step,
    facility: Facility,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    const facilityId = facility.facilityId;

    return (applyFacility(currentPayload, facility) as Observable<UNAVariationReviewRequestTaskPayload>).pipe(
      map((currentPayload) =>
        produce(currentPayload, (payload) => {
          payload.reviewSectionsCompleted[facilityId] = TaskItemStatus.UNDECIDED;
          delete payload.reviewSectionsCompleted[OVERALL_DECISION_SUBTASK];

          if (payload.determination) {
            delete payload.determination.type;

            if (payload.determination.type === 'REJECTED') {
              delete payload.determination.reason;
            }
          }
        }),
      ),
    );
  }
}
