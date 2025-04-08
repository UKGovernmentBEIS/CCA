import { map, Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyTp6AddTargets,
  applyTp6BaselineData,
  applyTp6TargetComposition,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  BaselineDataUserInput,
  OVERALL_DECISION_SUBTASK,
  TargetCompositionUserInput,
  TaskItemStatus,
  UNAVariationReviewRequestTaskPayload,
} from '@requests/common';
import { produce } from 'immer';

import { Targets } from 'cca-api';

export class Tp6PayloadMutator extends PayloadMutator {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS;

  apply(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    step: BaseLineAndTargetsReviewStep,
    userInput: unknown,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    switch (step) {
      case BaseLineAndTargetsReviewStep.TARGET_COMPOSITION:
        return this.applyTargetComposition(currentPayload, step, userInput as TargetCompositionUserInput);

      case BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA:
        return this.applyBaselineData(currentPayload, step, userInput as BaselineDataUserInput);

      case BaseLineAndTargetsReviewStep.ADD_TARGETS:
        return this.applyAddTargets(currentPayload, step, userInput as Targets);
    }
  }

  applyTargetComposition(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.TARGET_COMPOSITION,
    userInput: TargetCompositionUserInput,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    return (
      applyTp6TargetComposition(
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

  applyBaselineData(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA,
    userInput: BaselineDataUserInput,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    return (
      applyTp6BaselineData(currentPayload, this.subtask, userInput) as Observable<UNAVariationReviewRequestTaskPayload>
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

  applyAddTargets(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.ADD_TARGETS,
    userInput: Targets,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    return (
      applyTp6AddTargets(currentPayload, this.subtask, userInput) as Observable<UNAVariationReviewRequestTaskPayload>
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
