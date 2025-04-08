import { of } from 'rxjs';

import { produce } from 'immer';

import { TaskItemStatus } from '../task-item-status';
import { UNAReviewRequestTaskPayload, UNAVariationReviewRequestTaskPayload } from './underlying-agreement.types';

export const submitReviewSideEffect =
  (step: string, subtask: string) =>
  (currentPayload: UNAVariationReviewRequestTaskPayload | UNAReviewRequestTaskPayload) => {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[subtask] = TaskItemStatus.COMPLETED;

        const decisionType = payload.reviewGroupDecisions?.[step];

        payload.reviewSectionsCompleted[subtask] =
          decisionType.type === 'ACCEPTED' ? TaskItemStatus.APPROVED : TaskItemStatus.REJECTED;
      }),
    );
  };
