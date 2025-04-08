import { Signal } from '@angular/core';

import { of } from 'rxjs';

import { produce } from 'immer';

import { TaskItemStatus } from '../../../task-item-status';
import { UNAReviewRequestTaskPayload, UNAVariationReviewRequestTaskPayload } from '../../underlying-agreement.types';

export const submitReviewFacilitySideEffect =
  (facilityId: Signal<string>) =>
  (currentPayload: UNAVariationReviewRequestTaskPayload | UNAReviewRequestTaskPayload) => {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[facilityId()] = TaskItemStatus.COMPLETED;

        const decisionType = payload.facilitiesReviewGroupDecisions?.[facilityId()];
        payload.reviewSectionsCompleted[facilityId()] =
          decisionType.type === 'ACCEPTED' ? TaskItemStatus.APPROVED : TaskItemStatus.REJECTED;
      }),
    );
  };
