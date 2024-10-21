import { EnvironmentProviders, makeEnvironmentProviders } from '@angular/core';

import { PAYLOAD_MUTATORS, SIDE_EFFECTS, TaskApiService, TaskService, WIZARD_FLOW_MANAGERS } from '@netz/common/forms';

import { AdminTerminationSubmitPayloadMutator } from './admin-termination-payload.mutator';
import { AdminTerminationTaskService } from './services/admin-termination-task.service';
import { AdminTerminationTaskApiService } from './services/admin-termination-task-api.service';
import { ReasonForAdminTerminationStepFlowManager } from './subtasks/reason-for-admin-termination/reason-for-admin-termination-step-flow-manager';
import { ReasonForAdminTerminationSubmitSideEffect } from './subtasks/reason-for-admin-termination/reason-for-admin-termination-submit-side-effects';

export function provideAdminTerminationTaskServices(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: TaskApiService, useClass: AdminTerminationTaskApiService },
    { provide: TaskService, useClass: AdminTerminationTaskService },
  ]);
}

export function provideAdminTerminationPayloadMutators(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: AdminTerminationSubmitPayloadMutator },
  ]);
}

export function provideAdminTerminationStepFlowManagers(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: ReasonForAdminTerminationStepFlowManager },
  ]);
}

export function provideAdminTerminationSideEffects(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: SIDE_EFFECTS, multi: true, useClass: ReasonForAdminTerminationSubmitSideEffect },
  ]);
}
