import { map, Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyVariationDetails,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  UNAVariationReviewRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementVariationDetails } from 'cca-api';

export class VariationDetailsPayloadMutator extends PayloadMutator {
  override subtask = VARIATION_DETAILS_SUBTASK;

  apply(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    step,
    userInput: UnderlyingAgreementVariationDetails,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    return (
      applyVariationDetails(currentPayload, this.subtask, userInput) as Observable<UNAVariationReviewRequestTaskPayload>
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
