import { EnvironmentProviders, makeEnvironmentProviders } from '@angular/core';

import { PAYLOAD_MUTATORS, SIDE_EFFECTS, TaskApiService, TaskService, WIZARD_FLOW_MANAGERS } from '@netz/common/forms';

import { AdminTerminationFinalDecisionPayloadMutator } from './admin-termination-final-decision-payload.mutator';
import { AdminTerminationFinalDecisionTaskService } from './services/admin-termination-final-decision-task.service';
import { AdminTerminationFinalDecisionTaskApiService } from './services/admin-termination-final-decision-task-api.service';
import { FinalDecisionReasonStepFlowManager } from './subtasks/final-decision-reason/final-decision-reason-step-flow-manager';
import { FinalDecisionReasonSubmitSideEffect } from './subtasks/final-decision-reason/final-decision-reason-submit-side-effect';

export function provideAdminTerminationFinalDecisionTaskServices(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: TaskApiService, useClass: AdminTerminationFinalDecisionTaskApiService },
    { provide: TaskService, useClass: AdminTerminationFinalDecisionTaskService },
  ]);
}

export function provideAdminTerminationFinalDecisionPayloadMutators(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: AdminTerminationFinalDecisionPayloadMutator },
  ]);
}

export function provideAdminTerminationFinalDecisionStepFlowManagers(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: FinalDecisionReasonStepFlowManager },
  ]);
}

export function provideAdminTerminationFinalDecisionSideEffects(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: SIDE_EFFECTS, multi: true, useClass: FinalDecisionReasonSubmitSideEffect },
  ]);
}
