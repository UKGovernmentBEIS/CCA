import { map, Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyTp5AddTargets,
  applyTp5BaselineData,
  applyTp5BaselineExists,
  applyTp5TargetComposition,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  BaselineDataUserInput,
  OVERALL_DECISION_SUBTASK,
  TargetCompositionUserInput,
  TargetPeriodExistUserInput,
  TaskItemStatus,
  UNAVariationReviewRequestTaskPayload,
} from '@requests/common';
import { produce } from 'immer';

import { Targets } from 'cca-api';

export class Tp5PayloadMutator extends PayloadMutator {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;

  apply(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    step: BaseLineAndTargetsReviewStep,
    userInput: unknown,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    switch (step) {
      case BaseLineAndTargetsReviewStep.BASELINE_EXISTS:
        return this.applyBaselineExists(currentPayload, step, userInput as TargetPeriodExistUserInput);

      case BaseLineAndTargetsReviewStep.TARGET_COMPOSITION:
        return this.applyTargetComposition(currentPayload, step, userInput as TargetCompositionUserInput);

      case BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA:
        return this.applyBaselineData(currentPayload, step, userInput as BaselineDataUserInput);

      case BaseLineAndTargetsReviewStep.ADD_TARGETS:
        return this.applyAddTargets(currentPayload, step, userInput as Targets);
    }
  }

  applyBaselineExists(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.BASELINE_EXISTS,
    userInput: TargetPeriodExistUserInput,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    return (applyTp5BaselineExists(currentPayload, userInput) as Observable<UNAVariationReviewRequestTaskPayload>).pipe(
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

  applyTargetComposition(
    currentPayload: UNAVariationReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.TARGET_COMPOSITION,
    userInput: TargetCompositionUserInput,
  ): Observable<UNAVariationReviewRequestTaskPayload> {
    return (
      applyTp5TargetComposition(
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
      applyTp5BaselineData(currentPayload, this.subtask, userInput) as Observable<UNAVariationReviewRequestTaskPayload>
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
      applyTp5AddTargets(currentPayload, this.subtask, userInput) as Observable<UNAVariationReviewRequestTaskPayload>
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
