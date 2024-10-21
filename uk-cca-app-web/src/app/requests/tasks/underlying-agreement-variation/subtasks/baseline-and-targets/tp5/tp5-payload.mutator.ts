import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyTp5AddTargets,
  applyTp5BaselineData,
  applyTp5BaselineExists,
  applyTp5TargetComposition,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  BaselineDataUserInput,
  TargetCompositionUserInput,
  TargetPeriodExistUserInput,
  UNAVariationRequestTaskPayload,
} from '@requests/common';

import { Targets } from 'cca-api';

export class Tp5PayloadMutator extends PayloadMutator {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;

  apply(
    currentPayload: UNAVariationRequestTaskPayload,
    step: BaseLineAndTargetsStep,
    userInput: unknown,
  ): Observable<UNAVariationRequestTaskPayload> {
    switch (step) {
      case BaseLineAndTargetsStep.BASELINE_EXISTS:
        return applyTp5BaselineExists(
          currentPayload,
          userInput as TargetPeriodExistUserInput,
        ) as Observable<UNAVariationRequestTaskPayload>;

      case BaseLineAndTargetsStep.TARGET_COMPOSITION:
        return applyTp5TargetComposition(
          currentPayload,
          this.subtask,
          userInput as TargetCompositionUserInput,
        ) as Observable<UNAVariationRequestTaskPayload>;

      case BaseLineAndTargetsStep.ADD_BASELINE_DATA:
        return applyTp5BaselineData(
          currentPayload,
          this.subtask,
          userInput as BaselineDataUserInput,
        ) as Observable<UNAVariationRequestTaskPayload>;

      case BaseLineAndTargetsStep.ADD_TARGETS:
        return applyTp5AddTargets(
          currentPayload,
          this.subtask,
          userInput as Targets,
        ) as Observable<UNAVariationRequestTaskPayload>;
    }
  }
}
