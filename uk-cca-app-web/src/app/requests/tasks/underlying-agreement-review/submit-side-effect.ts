import { Signal } from '@angular/core';

import { of } from 'rxjs';

import { TaskItemStatus, UNAReviewRequestTaskPayload } from '@requests/common';
import produce from 'immer';

import { UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload } from 'cca-api';
type ReviewGroup = UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group'];

export const submitReviewSideEffect =
  (step: ReviewGroup, subtask: string) => (currentPayload: UNAReviewRequestTaskPayload) => {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[subtask] = TaskItemStatus.COMPLETED;
        const decisionType = payload.reviewGroupDecisions?.[step];
        if (decisionType.type === 'ACCEPTED') {
          payload.reviewSectionsCompleted[subtask] = TaskItemStatus.APPROVED;
        } else {
          payload.reviewSectionsCompleted[subtask] = TaskItemStatus.REJECTED;
        }
      }),
    );
  };

export const submitReviewFacilitySideEffect =
  (facilityId: Signal<string>) => (currentPayload: UNAReviewRequestTaskPayload) => {
    return of(
      produce(currentPayload, (payload) => {
        payload.sectionsCompleted[facilityId()] = TaskItemStatus.COMPLETED;
        const decisionType = payload.facilitiesReviewGroupDecisions?.[facilityId()];
        if (decisionType.type === 'ACCEPTED') {
          payload.reviewSectionsCompleted[facilityId()] = TaskItemStatus.APPROVED;
        } else {
          payload.reviewSectionsCompleted[facilityId()] = TaskItemStatus.REJECTED;
        }
      }),
    );
  };
