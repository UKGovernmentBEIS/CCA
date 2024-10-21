import { Observable, of, switchMap } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyTp6AddTargets,
  applyTp6BaselineData,
  applyTp6TargetComposition,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  BaselineDataUserInput,
  TargetCompositionUserInput,
  TaskItemStatus,
  UNAReviewRequestTaskPayload,
} from '@requests/common';
import produce from 'immer';

import { Targets } from 'cca-api';

export class Tp6PayloadMutator extends PayloadMutator {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS;

  apply(
    currentPayload: UNAReviewRequestTaskPayload,
    step: BaseLineAndTargetsReviewStep,
    userInput: unknown,
  ): Observable<UNAReviewRequestTaskPayload> {
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
    currentPayload: UNAReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.TARGET_COMPOSITION,
    userInput: TargetCompositionUserInput,
  ): Observable<UNAReviewRequestTaskPayload> {
    return (
      applyTp6TargetComposition(currentPayload, this.subtask, userInput) as Observable<UNAReviewRequestTaskPayload>
    ).pipe(
      switchMap((currentPayload) =>
        of(
          produce(currentPayload, (payload) => {
            payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
          }),
        ),
      ),
    );
  }

  applyBaselineData(
    currentPayload: UNAReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA,
    userInput: BaselineDataUserInput,
  ): Observable<UNAReviewRequestTaskPayload> {
    return (
      applyTp6BaselineData(currentPayload, this.subtask, userInput) as Observable<UNAReviewRequestTaskPayload>
    ).pipe(
      switchMap((currentPayload) =>
        of(
          produce(currentPayload, (payload) => {
            payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
          }),
        ),
      ),
    );
  }

  applyAddTargets(
    currentPayload: UNAReviewRequestTaskPayload,
    _: BaseLineAndTargetsReviewStep.ADD_TARGETS,
    userInput: Targets,
  ): Observable<UNAReviewRequestTaskPayload> {
    return (
      applyTp6AddTargets(currentPayload, this.subtask, userInput) as Observable<UNAReviewRequestTaskPayload>
    ).pipe(
      switchMap((currentPayload) =>
        of(
          produce(currentPayload, (payload) => {
            payload.reviewSectionsCompleted[this.subtask] = TaskItemStatus.UNDECIDED;
          }),
        ),
      ),
    );
  }
}
