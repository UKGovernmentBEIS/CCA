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
  TargetCompositionUserInput,
  TargetPeriodExistUserInput,
  TaskItemStatus,
  UNAReviewRequestTaskPayload,
} from '@requests/common';
import produce from 'immer';

import { Targets } from 'cca-api';

export class Tp5PayloadMutator extends PayloadMutator {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;

  apply(
    currentPayload: UNAReviewRequestTaskPayload,
    step: BaseLineAndTargetsReviewStep,
    userInput: unknown,
  ): Observable<UNAReviewRequestTaskPayload> {
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
    currentPayload: UNAReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.BASELINE_EXISTS,
    userInput: TargetPeriodExistUserInput,
  ): Observable<UNAReviewRequestTaskPayload> {
    return (applyTp5BaselineExists(currentPayload, userInput) as Observable<UNAReviewRequestTaskPayload>).pipe(
      map((currentPayload) =>
        produce(currentPayload, (payload) => {
          payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
        }),
      ),
    );
  }

  applyTargetComposition(
    currentPayload: UNAReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.TARGET_COMPOSITION,
    userInput: TargetCompositionUserInput,
  ): Observable<UNAReviewRequestTaskPayload> {
    return (
      applyTp5TargetComposition(currentPayload, this.subtask, userInput) as Observable<UNAReviewRequestTaskPayload>
    ).pipe(
      map((currentPayload) =>
        produce(currentPayload, (payload) => {
          payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
        }),
      ),
    );
  }

  applyBaselineData(
    currentPayload: UNAReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA,
    userInput: BaselineDataUserInput,
  ): Observable<UNAReviewRequestTaskPayload> {
    return (
      applyTp5BaselineData(currentPayload, this.subtask, userInput) as Observable<UNAReviewRequestTaskPayload>
    ).pipe(
      map((currentPayload) =>
        produce(currentPayload, (payload) => {
          payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
        }),
      ),
    );
  }

  applyAddTargets(
    currentPayload: UNAReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.ADD_TARGETS,
    userInput: Targets,
  ): Observable<UNAReviewRequestTaskPayload> {
    return (
      applyTp5AddTargets(currentPayload, this.subtask, userInput) as Observable<UNAReviewRequestTaskPayload>
    ).pipe(
      map((currentPayload) =>
        produce(currentPayload, (payload) => {
          payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
        }),
      ),
    );
  }
}
