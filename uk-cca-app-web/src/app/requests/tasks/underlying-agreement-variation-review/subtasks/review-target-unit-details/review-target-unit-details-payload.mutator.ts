import { map, Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyTargetUnitDetails,
  OVERALL_DECISION_SUBTASK,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  UNAVariationReviewRequestTaskPayload,
} from '@requests/common';
import { produce } from 'immer';

export class ReviewTargetUnitDetailsPayloadMutator extends PayloadMutator {
  override subtask = REVIEW_TARGET_UNIT_DETAILS_SUBTASK;

  /**
   * @param currentPayload
   * @param userInput The form value of each step
   */
  apply(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    step,
    userInput: any,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    return (
      applyTargetUnitDetails(
        currentPayload,
        this.subtask,
        userInput,
      ) as Observable<UNAVariationReviewRequestTaskPayload>
    ).pipe(
      map((currentPayload) =>
        produce(currentPayload, (payload) => {
          payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
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
