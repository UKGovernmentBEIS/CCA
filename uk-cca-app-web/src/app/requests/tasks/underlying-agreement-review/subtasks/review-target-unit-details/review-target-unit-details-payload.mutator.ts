import { map, Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyTargetUnitDetails,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  UNAReviewRequestTaskPayload,
} from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementReviewRequestTaskPayload } from 'cca-api';

export class ReviewTargetUnitDetailsPayloadMutator extends PayloadMutator {
  override subtask = REVIEW_TARGET_UNIT_DETAILS_SUBTASK;

  /**
   * @param currentPayload
   * @param userInput The form value of each step
   */
  apply(
    currentPayload: UnderlyingAgreementReviewRequestTaskPayload,
    step,
    userInput: any,
  ): Observable<UnderlyingAgreementReviewRequestTaskPayload> {
    return (
      applyTargetUnitDetails(currentPayload, this.subtask, userInput) as Observable<UNAReviewRequestTaskPayload>
    ).pipe(
      map((currentPayload) =>
        produce(currentPayload, (payload) => {
          payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
        }),
      ),
    );
  }
}
