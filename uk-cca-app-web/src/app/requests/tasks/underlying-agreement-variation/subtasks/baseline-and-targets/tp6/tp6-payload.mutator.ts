import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyTp6AddTargets,
  applyTp6BaselineData,
  applyTp6TargetComposition,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  BaselineDataUserInput,
  TargetCompositionUserInput,
  UNAVariationRequestTaskPayload,
} from '@requests/common';

import { Targets } from 'cca-api';

export class Tp6PayloadMutator extends PayloadMutator {
  override subtask = BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS;

  apply(
    currentPayload: UNAVariationRequestTaskPayload,
    step: BaseLineAndTargetsStep,
    userInput: unknown,
  ): Observable<UNAVariationRequestTaskPayload> {
    switch (step) {
      case BaseLineAndTargetsStep.TARGET_COMPOSITION:
        return applyTp6TargetComposition(
          currentPayload,
          this.subtask,
          userInput as TargetCompositionUserInput,
        ) as Observable<UNAVariationRequestTaskPayload>;

      case BaseLineAndTargetsStep.ADD_BASELINE_DATA:
        return applyTp6BaselineData(
          currentPayload,
          this.subtask,
          userInput as BaselineDataUserInput,
        ) as Observable<UNAVariationRequestTaskPayload>;

      case BaseLineAndTargetsStep.ADD_TARGETS:
        return applyTp6AddTargets(
          currentPayload,
          this.subtask,
          userInput as Targets,
        ) as Observable<UNAVariationRequestTaskPayload>;
    }
  }
}
