import { Signal } from '@angular/core';

import { of } from 'rxjs';

import { TaskItemStatus, UNAVariationReviewRequestTaskPayload } from '@requests/common';
import produce from 'immer';

import { UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload } from 'cca-api';
type ReviewGroup = UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'];

export const submitReviewSideEffect =
  (step: ReviewGroup, subtask: string) => (currentPayload: UNAVariationReviewRequestTaskPayload) => {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[subtask] = TaskItemStatus.COMPLETED;

        const decisionType = payload.reviewGroupDecisions?.[step];
        payload.reviewSectionsCompleted[subtask] =
          decisionType.type === 'ACCEPTED' ? TaskItemStatus.APPROVED : TaskItemStatus.REJECTED;
      }),
    );
  };

export const submitReviewFacilitySideEffect =
  (facilityId: Signal<string>) => (currentPayload: UNAVariationReviewRequestTaskPayload) => {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[facilityId()] = TaskItemStatus.COMPLETED;

        const decisionType = payload.facilitiesReviewGroupDecisions?.[facilityId()];
        payload.reviewSectionsCompleted[facilityId()] =
          decisionType.type === 'ACCEPTED' ? TaskItemStatus.APPROVED : TaskItemStatus.REJECTED;
      }),
    );
  };
